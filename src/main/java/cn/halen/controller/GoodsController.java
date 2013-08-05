package cn.halen.controller;

import java.io.*;
import java.util.*;

import cn.halen.data.mapper.MySkuMapper;
import cn.halen.exception.InsufficientStockException;
import cn.halen.service.SkuService;
import cn.halen.service.excel.ExcelReader;
import cn.halen.service.excel.GoodsExcelReader;
import cn.halen.service.excel.GoodsRow;
import cn.halen.service.excel.Row;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.mapper.GoodsMapper;
import cn.halen.data.pojo.Goods;
import cn.halen.data.pojo.MySku;
import cn.halen.service.GoodsService;
import cn.halen.service.ResultInfo;
import cn.halen.service.top.ItemClient;
import cn.halen.service.top.TopConfig;
import cn.halen.util.Constants;
import cn.halen.util.Paging;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@Controller
public class GoodsController {
	
	private Logger log = LoggerFactory.getLogger(GoodsController.class);

	@Autowired
	private GoodsMapper goodsMapper;
	
	@Autowired
	private TopConfig topConfig;
	
	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	private AdminMapper adminMapper;
	
	@Autowired
	private ItemClient itemClient;

    @Autowired
    private SkuService skuService;

    @Autowired
    private MySkuMapper skuMapper;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;

	@RequestMapping(value="goods/goods_list")
	public String list(Model model, @RequestParam(value="page", required=false) Integer page,
			@RequestParam(value="goods_id", required=false) String goodsId) {
		int intPage = 1;
		if(null!=page && page>0) {
			intPage = page;
		}
		if(null != goodsId) {
			goodsId = goodsId.trim();
		}
		model.addAttribute("goods_id", goodsId);
		long totalCount = goodsMapper.countGoodsPaging(goodsId);
		Paging paging = new Paging(intPage, 10, totalCount);
		model.addAttribute("paging", paging);
		model.addAttribute("totalCount", totalCount);
		if(0 == totalCount) {
			return "goods/goods_list";
		}
		
		List<Goods> list = goodsMapper.listGoodsDetail(paging.getStart(), paging.getPageSize(), goodsId);
		if(null == list || list.size() == 0) {
			return "goods/goods_list";
		}
		
		//<Goods, <颜色(编号), <尺码, 可用数量/实际数量>>>
		Map<String, Map<String, Map<String, String>>> map = new HashMap<String, Map<String, Map<String, String>>>();
		for(Goods goods : list) {
			Map<String, Map<String, String>> map2 = new HashMap<String, Map<String, String>>();
			map.put(goods.getHid(), map2);
			List<MySku> skuList = goods.getSkuList();
			//按size排序sku
			Collections.sort(skuList, new Comparator<MySku>() {

				@Override
				public int compare(MySku o1, MySku o2) {
					
					String size1 = o1.getSize();
					String size2 = o2.getSize();
					int len1 = size1.length();
					int len2 = size2.length();
					int n = Math.min(len1, len2);
					char[] v1 = size1.toCharArray();
					char[] v2 = size2.toCharArray();
					int k = 0;
					while(k < n) {
						char c1 = v1[k];
						char c2 = v2[k];
						if(c1 != c2) {
							return c1 - c2;
						}
						k++;
					}
					return len1 - len2;
				}
			});
			
			for(MySku sku : skuList) {
				String color = sku.getColor();
                String hid = sku.getHid();
                String colorId = hid.substring(0, 2);
                String colorAndId = color + "(" + colorId + ")";
				Map<String, String> map3 = map2.get(colorAndId);
				if(null == map3) {
					map3 = new LinkedHashMap<String, String>();
					map2.put(colorAndId, map3);
				}
				map3.put(sku.getSize(), sku.getQuantity()-sku.getLock_quantity() + "/" + sku.getQuantity());
			}
		}
		
		model.addAttribute("map", map);
		model.addAttribute("list", list);
		model.addAttribute("templateList", adminMapper.selectTemplateNameAll());
		
		return "goods/goods_list";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="goods/action/batch_change")
	public @ResponseBody ResultInfo syncStore(Model model, @RequestParam("hids") String hids, @RequestParam("action") String action,
			@RequestParam(value="template", required=false) String template) {
		ResultInfo result = new ResultInfo();
		if(StringUtils.isEmpty(hids.trim())) {
			result.setSuccess(false);
			result.setErrorInfo("请至少选择一个商品!");
			return result;
		}
		String[] hidArr = hids.split(";");
		List<String> hidList = new ArrayList<String>();
		for(String hid : hidArr) {
			if(StringUtils.isNotEmpty(hid)) {
				hidList.add(hid);
			}
		}
		try {
			if("sync-store".equals(action)) {
				List<Goods> goodsList =	goodsMapper.selectById(hidList);
				for(Goods goods : goodsList) {
					List<MySku> skuList = goods.getSkuList();
					for(MySku sku : skuList) {
						String key = goods.getHid() + ";;;" + sku.getColor() + ";;;" + sku.getSize(); 
						redisTemplate.opsForSet().add(Constants.REDIS_SKU_GOODS_SET, key);
					}
				}
				//notify listener to handler
				redisTemplate.convertAndSend(Constants.REDIS_SKU_GOODS_CHANNEL, "1");
			} else if("sync-pic".equals(action)) {
				itemClient.updatePic(hidList);
			} else if("change-template".equals(action)) {
				goodsMapper.updateTemplate(hidList, template);
			}
		} catch(Exception e) {
			log.error("", e);
			result.setSuccess(false);
			result.setErrorInfo("系统异常，请重试!");
		}
		return result;
	}

    @RequestMapping(value="goods/action/upload")
    public String upload(Model model, @RequestParam("action") String action) {
        model.addAttribute("action", action);
        return "goods/upload";
    }

    @RequestMapping(value="goods/action/do_upload")
    public String doUpload(Model model, @RequestParam("action") String action,
                           @RequestParam("file") MultipartFile file) {
        model.addAttribute("action", action);
        String actionName = null;
        if(!file.isEmpty()) {
            String type = file.getContentType();
            if(!"application/vnd.ms-excel".equals(type)) {
                model.addAttribute("errorInfo", "选择的文件必须是03版本的excel表格!");
                return "goods/upload";
            }
            try {
                String fileName = new String(file.getOriginalFilename().getBytes("iso8859-1"), "UTF-8");
                File dest = null;
                if("buy".equals(action)) {
                    dest = new File(topConfig.getFileBuyGoods() + "\\" + fileName);
                    actionName = "进仓单";
                } else if("refund".equals(action)) {
                    dest = new File(topConfig.getFileRefundGoods() + "\\" + fileName);
                    actionName = "出仓单";
                } else if("new".equals(action)) {
                    dest = new File(topConfig.getFileNewGoods() + "\\" + fileName);
                    actionName = "新建商品单";
                } else {
                    model.addAttribute("errorInfo", "无效参数!");
                    return "goods/upload";
                }
                if(dest.exists()) {
                    model.addAttribute("errorInfo", "这个" + actionName + "已经存在，不能重复添加!");
                    return "goods/upload";
                }
                byte[] bytes = file.getBytes();
                OutputStream out = new FileOutputStream(dest);
                out.write(bytes);
                out.flush();
                out.close();
                boolean handleResult = action.equals("new")==false? handleExcel(model, dest, action) : handleExcel4New(model, dest);
                if(!handleResult) {
                    dest.delete();
                    return "goods/upload";
                }
            } catch (IOException e) {
                log.error("Upload file failed, ", e);
                model.addAttribute("errorInfo", "上传文件失败，请重试!");
                return "goods/upload";
            }
        } else {
            model.addAttribute("errorInfo", "必须选择一个文件!");
            return "goods/upload";
        }
        model.addAttribute("successInfo", "导入" + actionName + "成功!");
        return "goods/upload";
    }

    private boolean handleExcel(Model model, File file, String action) {
        ExcelReader reader = null;
        List<Row> rows = null;
        try {
            reader = new ExcelReader(file);
            boolean checkColumn = reader.checkColumn();
            if(!checkColumn) {
                model.addAttribute("errorInfo", "格式不正确，必须有 编号、颜色、尺码、数量 这几列!");
                return false;
            }
            int checkData = reader.checkData();
            if(checkData != 0) {
                model.addAttribute("errorInfo", "第 " + (checkData + 1) + " 行数据格式不正确，请修改后重试!");
                return false;
            }
            rows = reader.getData();
        } catch (Exception e) {
            log.error("Handle excel failed, ", e);
            model.addAttribute("errorInfo", "系统异常，请联系管理员!");
            return false;
        } finally {
            reader.destroy();
        }

        Row row = skuService.checkRow(rows);
        if(null != row) {
            model.addAttribute("errorInfo", "这个商品(" + row.getGoodsId() + "," + row.getColor() + "," + row.getSize() + ")不存在，请检查是否存在错误或者在系统中添加该商品之后重试!");
            return false;
        }
        try {
            skuService.execRow(rows, action);
        } catch (InsufficientStockException e) {
            model.addAttribute("errorInfo", "这个商品(" + e.getGoodsHid() + ")库存不足，更新失败!");
            return false;
        } catch (Exception e) {
            model.addAttribute("errorInfo", "系统异常，请重试!");
            return false;
        }
        return true;
    }

    private boolean handleExcel4New(Model model, File file) {
        GoodsExcelReader reader = null;
        List<GoodsRow> rows = null;
        try {
            reader = new GoodsExcelReader(file);
            boolean checkColumn = reader.checkColumn();
            if(!checkColumn) {
                model.addAttribute("errorInfo", "格式不正确，必须有 编号、名称、价格、颜色编码、颜色、尺码 这几列!");
                return false;
            }
            int checkData = reader.checkData();
            if(checkData != 0) {
                model.addAttribute("errorInfo", "第 " + (checkData + 1) + " 行数据格式不正确，请修改后重试!");
                return false;
            }
            rows = reader.getData();
        } catch (Exception e) {
            log.error("Handle excel failed, ", e);
            model.addAttribute("errorInfo", "系统异常，请联系管理员!");
            return false;
        } finally {
            reader.destroy();
        }

        String result = goodsService.checkRow(rows);
        if(null != result) {
            model.addAttribute("errorInfo", result);
            return false;
        }
        try {
            goodsService.execRow(rows);
        } catch (Exception e) {
            model.addAttribute("errorInfo", "系统异常，请重试!");
            log.error("error", e);
            return false;
        }
        return true;
    }

    @RequestMapping(value="goods/upload_list")
    public String uploadList(Model model, @RequestParam("action") String action) {
        File filePath = null;
        model.addAttribute("action", action);
        if("buy".equals(action)) {
            filePath = new File(topConfig.getFileBuyGoods());
        } else if ("refund".equals(action)) {
            filePath = new File(topConfig.getFileRefundGoods());
        } else if("new".equals(action)) {
            filePath = new File(topConfig.getFileNewGoods());
        } else {
            model.addAttribute("errorInfo", "参数错误!");
            return "error_page";
        }
        if(filePath.exists()) {
            File[] files = filePath.listFiles();
            List<File> fileList = Arrays.asList(files);
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    long date1 = o1.lastModified();
                    long date2 = o2.lastModified();
                    if(date1 > date2) {
                        return -1;
                    } else if(date1 < date2) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            Map<String, Date> map = new LinkedHashMap<String, Date>(fileList.size());
            for(File file : fileList) {
                map.put(file.getName(), new Date(file.lastModified()));
            }
            model.addAttribute("map", map);
        }
        return "goods/upload_list";
    }

    @RequestMapping("goods/download")
    public void download(Model model, HttpServletResponse response, @RequestParam("name") String name,
                         @RequestParam("action") String action) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        File file = null;
        if("buy".equals(action)) {
            file = new File(topConfig.getFileBuyGoods() + "//" + name);
        } else if("new".equals(action)) {
            file = new File(topConfig.getFileNewGoods() + "//" + name);
        } else if ("refund".equals(action)) {
            file = new File(topConfig.getFileRefundGoods() + "//" + name);
        }
        if(!file.exists()) {
            log.info("File {} not exists, can not be download!");
            return;
        }

        try {
            response.setHeader("Content-Disposition", "attachment;fileName=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
        }
        OutputStream os = null;
        try {
            response.flushBuffer();
            os = response.getOutputStream();
            byte[] b=new byte[1024];
            int length;
            InputStream is = new FileInputStream(file);
            while((length = is.read(b))>0){
                os.write(b, 0, length);
            }
        } catch (IOException e) {
            log.error("", e);
        } finally {
            try {
                os.close();
            } catch (IOException e) {
            }
        }
    }

    @RequestMapping(value="goods/action/change_goods")
    public @ResponseBody ResultInfo changeGoods(Model model, @RequestParam("hid") String hid, @RequestParam("type") String type,
                                                @RequestParam("value") String value, @RequestParam("oldValue") String oldValue) {
        ResultInfo result = new ResultInfo();
        try {
            if("color".equals(type)) {
                List<MySku> list = skuMapper.selectByGoodsIdColor(hid, value);
                if(list.size()>0) {
                    result.setSuccess(false);
                    result.setErrorInfo("更新失败，颜色" + value + "已经存在!");
                } else {
                    skuMapper.updateColorByGoodsIdColor(hid, oldValue, value);
                }
            } else if("size".equals(type)) {
                List<MySku> list = skuMapper.selectByGoodsIdSize(hid, value);
                if(list.size()>0) {
                    result.setSuccess(false);
                    result.setErrorInfo("更新失败，尺寸" + value + "已经存在!");
                } else {
                    skuMapper.updateSizeByGoodsIdSize(hid, oldValue, value);
                }
            } else {
                result.setErrorInfo("参数错误!");
                result.setSuccess(false);
            }
        } catch (Exception e) {
            result.setErrorInfo("系统异常，请重试!");
            result.setSuccess(false);
            log.error("更新商品属性失败,", e);
        }
        return result;
    }
}
