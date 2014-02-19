package cn.halen.controller;

import java.io.*;
import java.util.*;

import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.pojo.Shop;
import cn.halen.exception.InsufficientBalanceException;
import cn.halen.exception.InsufficientStockException;
import cn.halen.exception.MyException;
import cn.halen.service.AdminService;
import cn.halen.service.SkuService;
import cn.halen.service.excel.ExcelReader;
import cn.halen.service.excel.GoodsExcelReader;
import cn.halen.service.excel.GoodsRow;
import cn.halen.service.excel.Row;
import cn.halen.service.yougou.YougouService;
import cn.halen.util.ErrorInfoHolder;
import com.taobao.api.ApiException;
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
    private AdminService adminService;

    @Autowired
    private MySkuMapper skuMapper;

    @Autowired
    private YougouService yougouService;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;

    private List<String> getSizeList(List<Goods> list) {
        List<String> result = new ArrayList<String>();
        Set<String> sizeSet = new HashSet<String>();
        for(Goods g : list) {
            for(MySku s : g.getSkuList()) {
                sizeSet.add(s.getSize());
            }
        }
        for(String size : sizeSet) {
            result.add(size);
        }
        Collections.sort(result);
        return result;
    }

    @RequestMapping(value="goods/export")
    public void export(Model model, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("gb2312");
        response.setContentType("multipart/form-data");

        List<Goods> list = goodsMapper.listAllGoodsDetail();
        List<String> sizeList = getSizeList(list);
        Map<String, Map<String, Map<String, Long>>> map = f2(list);

        Date now = new Date();
        String fileName = "store-" + now.getTime() + ".csv";
        File f = new File(topConfig.getFileExport() + File.separator + fileName);
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "gb2312"));
        String title = "货号,颜色";
        for(String size : sizeList) {
            title += "," + size;
        }
        w.write(title);
        w.write("\r\n");
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<String, Map<String, Map<String, Long>>> e : map.entrySet()) {

            String itemId = e.getKey();
            for(Map.Entry<String, Map<String, Long>> e2 : e.getValue().entrySet()) {
                String color = e2.getKey();
                Map<String, Long> sizeMap = e2.getValue();
                builder.delete(0, builder.length());//清空builder
                builder.append(itemId).append(",").append(color);
                for(String size : sizeList) {

                    builder.append(",").append(sizeMap.get(size)==null?"" : sizeMap.get(size));
                }
                w.write(builder.toString());
                w.write("\r\n");
            }
        }
        w.flush();
        w.close();

        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
        response.flushBuffer();

        OutputStream os=response.getOutputStream();
        InputStream in = new FileInputStream(f);
        try {
            byte[] b=new byte[1024];
            int length;
            while((length=in.read(b))>0){
                os.write(b,0,length);
            }
        }catch (IOException e) {
            log.error("export store failed.", e);
        }finally {
            os.close();
        }
    }

    @RequestMapping(value="goods/action/lock_list")
    public String lockList(Model model, @RequestParam(value="page", required=false) Integer page,
                       @RequestParam(value="goods_id", required=false) String goodsId) {

        int intPage = 1;
        if(null!=page && page>0) {
            intPage = page;
        }
        if(null != goodsId) {
            goodsId = goodsId.trim();
        }
        model.addAttribute("goods_id", goodsId);
        long totalCount = skuMapper.countManaualLock(goodsId);
        Paging paging = new Paging(intPage, 100, totalCount);
        model.addAttribute("paging", paging);
        model.addAttribute("totalCount", totalCount);
        if(0 == totalCount) {
            return "goods/lock_list";
        }

        List<MySku> list = skuMapper.selectManaualLock(paging, goodsId);
        if(null == list || list.size() == 0) {
            return "goods/lock_list";
        }
        model.addAttribute("list", list);
        return "goods/lock_list";
    }

    @RequestMapping(value="goods/action/change_manaual_lock")
    public void changeManaualLock(HttpServletResponse resp, @RequestParam(value="page", required=false) Integer page,
                                    @RequestParam(value="goods_id", required=false) String goodsId, @RequestParam long id, @RequestParam String action) {
        MySku sku = skuMapper.select(id);
        try {
            if(null != sku) {
                if("unlock".equals(action)) {
                    skuService.updateSku(id, 0, 0, -Math.abs(sku.getManaual_lock_quantity()), true);
                } else if("refund".equals(action)) {
                    skuService.updateSku(id, -Math.abs(sku.getManaual_lock_quantity()), 0, -Math.abs(sku.getManaual_lock_quantity()), true);
                }
            }
        } catch (InsufficientStockException e) {
            log.error("changeManaualLock error", e);
        }
        try {
            resp.sendRedirect("/goods/action/lock_list?page=" + page + "&goods_id=" + goodsId);
        } catch (IOException e) {
        }
    }

    @RequestMapping(value="goods/action/batch_change_manaual_lock")
    public @ResponseBody ResultInfo batch_change_manaual_lock(Model model, @RequestParam String ids, @RequestParam("action") String action) {

        ResultInfo result = new ResultInfo();
        if(StringUtils.isNotBlank(ids)) {
            String[] idArr = ids.split(",");
            for(String id : idArr) {
                if(StringUtils.isNotBlank(id)) {
                    try {
                        MySku sku = skuMapper.select(Long.parseLong(id));
                        if(null != sku) {
                            if("unlock".equals(action)) {
                                skuService.updateSku(Long.parseLong(id), 0, 0, -Math.abs(sku.getManaual_lock_quantity()), true);
                            } else if("refund".equals(action)) {
                                skuService.updateSku(Long.parseLong(id), -Math.abs(sku.getManaual_lock_quantity()), 0, -Math.abs(sku.getManaual_lock_quantity()), true);
                            }
                        }
                    } catch (InsufficientStockException e) {
                        log.error("batch_change_manaual_lock InsufficientStockException", e);
                    } catch (Exception e) {
                        log.error("batch_change_manaual_lock error", e);
                    }
                }
            }
        }
        return result;
    }

	@RequestMapping(value="goods/goods_list")
	public String list(Model model, @RequestParam(value="page", required=false) Integer page, @RequestParam(required = false) String from,
			@RequestParam(value="goods_id", required=false) String goodsId, @RequestParam(value="tid", required=false) String tid,
            @RequestParam(required = false, defaultValue = "1") int status) {

        model.addAttribute("quantity", skuMapper.sumQuantity());
        model.addAttribute("lockQuantity", skuMapper.sumLockQuantity());

		int intPage = 1;
		if(null!=page && page>0) {
			intPage = page;
		}
		if(null != goodsId) {
			goodsId = goodsId.trim();
		}
		model.addAttribute("goods_id", goodsId);
		long totalCount = goodsMapper.countGoodsPaging(goodsId, status);
		Paging paging = new Paging(intPage, 10, totalCount);
		model.addAttribute("paging", paging);
		model.addAttribute("totalCount", totalCount);
		if(0 == totalCount) {
			return "goods/goods_list";
		}
		
		List<Goods> list = goodsMapper.listGoodsDetail(paging.getStart(), paging.getPageSize(), goodsId, status);
		if(null == list || list.size() == 0) {
			return "goods/goods_list";
		}
        f(list, model);
		model.addAttribute("list", list);
		model.addAttribute("templateList", adminMapper.selectTemplateNameAll());

        model.addAttribute("tid", tid);
        model.addAttribute("from", from);
        model.addAttribute("status", status);

        model.addAttribute("shopList", adminService.getSyncShopList());

		return "goods/goods_list";
	}

    /**
     * 用于导出库存
     * @param list
     * @return
     */
    public Map<String, Map<String, Map<String, Long>>> f2(List<Goods> list) {
        //<Goods, <颜色, <尺码, 可用数量>>>
        Map<String, Map<String, Map<String, Long>>> map = new HashMap<String, Map<String, Map<String, Long>>>();
        for(Goods goods : list) {
            Map<String, Map<String, Long>> map2 = new HashMap<String, Map<String, Long>>();
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
                Map<String, Long> map3 = map2.get(color);
                if(null == map3) {
                    map3 = new LinkedHashMap<String, Long>();
                    map2.put(color, map3);
                }
                long salable = sku.getQuantity() - sku.getLock_quantity() - sku.getManaual_lock_quantity(); //可用库存
                map3.put(sku.getSize(), salable);
            }
        }

        return map;
    }

    public Map<String, Map<String, Map<String, String>>> f(List<Goods> list, Model model) {
        //<Goods, <颜色(编号), <尺码, 可用数量/实际数量>>>
        Map<String, Map<String, Map<String, String>>> map = new HashMap<String, Map<String, Map<String, String>>>();
        //<hid, 可用数量/实际数量> 或者 <hid-color, 可用数量/实际数量>
        Map<String, String> goodsCount = new HashMap<String, String>();
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

            long q = 0;
            long lockQ = 0;
            for(MySku sku : skuList) {
                String color = sku.getColor();
                String colorId = sku.getColor_id();
                String colorAndId = color + "()";
                if(null != colorId) {
                    colorAndId = color + "(" + colorId + ")";
                }
                Map<String, String> map3 = map2.get(colorAndId);
                if(null == map3) {
                    map3 = new LinkedHashMap<String, String>();
                    map2.put(colorAndId, map3);
                }
                long salable = sku.getQuantity() - sku.getLock_quantity() - sku.getManaual_lock_quantity();  //可售库存
                map3.put(sku.getSize(), salable + "/" + sku.getQuantity());

                q += sku.getQuantity();
                lockQ += sku.getLock_quantity();
            }
            goodsCount.put(goods.getHid(), (q-lockQ) + "/" + q);
        }

        for(Map.Entry<String, Map<String, Map<String, String>>> e : map.entrySet()) {
            String hid = e.getKey();
            for(Map.Entry<String, Map<String, String>> e2 : e.getValue().entrySet()) {
                String color = e2.getKey();
                long q = 0;
                long savedQ = 0;
                for(Map.Entry<String, String> e3 : e2.getValue().entrySet()) {
                    String[] ss = e3.getValue().split("/");
                    savedQ += Long.parseLong(ss[0]);
                    q += Long.parseLong(ss[1]);
                }
                goodsCount.put(hid+color, savedQ + "/" + q);
            }
        }
        if(null != model) {
            model.addAttribute("map", map);
            model.addAttribute("goodsCount", goodsCount);
        }
        return map;
    }
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="goods/batch_change")
	public @ResponseBody ResultInfo batchChange(Model model, @RequestParam("hids") String hids, @RequestParam("action") String action,
			@RequestParam(value="template", required=false) String template, @RequestParam(required = false) String shops) {
		ResultInfo result = new ResultInfo();
		if(StringUtils.isEmpty(hids.trim()) && !"sync-store-all".equals(action)) {
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
            if("sync-store".equals(action) || "sync-store-all".equals(action)) {
                if(StringUtils.isBlank(shops)) {
                    result.setSuccess(false);
                    result.setErrorInfo("请选择店铺");
                }
                Shop shop = adminMapper.selectShopBySellerNick(shops);
				List<Goods> goodsList =	null;
                if("sync-store".equals(action)) {
                    goodsList = goodsMapper.selectById(hidList);
                } else if("sync-store-all".equals(action)) {
                    goodsList = goodsMapper.selectMapByStatus();
                }
                StringBuilder builder = new StringBuilder();
				for(Goods goods : goodsList) {
					List<MySku> skuList = goods.getSkuList();
                    String res = null;
					for(MySku sku : skuList) {

                        if(Constants.SHOP_THPE_YOUGOU.equals(shop.getType())) {
                            res = yougouService.updateInventory(sku, shop);
                        } else if(Constants.SHOP_TYPE_TAOBAO.equals(shop.getType()) || Constants.SHOP_TYPE_TIANMAO.equals(shop.getType())) {
                            res = goodsService.updateSkuQuantity(sku, shop);
                        }
                        if(StringUtils.isNotBlank(res)) {
                            builder.append(sku.getGoods_id()).append(sku.getColor_id()).append(sku.getSize())
                                    .append("更新库存失败，原因：").append(res).append("\r\n");
                        }
					}
                }
                if(builder.length() > 0) {
                    result.setErrorInfo(builder.toString());
                    result.setSuccess(false);
                    return result;
                }
				//notify listener to handler
			} else if("sync-pic".equals(action)) {
				itemClient.updatePic(hidList);
			} else if("change-template".equals(action)) {
				goodsMapper.updateTemplate(hidList, template);
			} else if("sold-out".equals(action)) {
                goodsMapper.updateStatus(hidList, 0);
            } else if("sold-on".equals(action)) {
                goodsMapper.updateStatus(hidList, 1);
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
        File dest = null;
        if(!file.isEmpty()) {
            String type = file.getContentType();
            if(!"application/vnd.ms-excel".equals(type)) {
                model.addAttribute("errorInfo", "选择的文件必须是03版本的excel表格!");
                return "goods/upload";
            }
            try {
                String fileName = new String(file.getOriginalFilename().getBytes("iso8859-1"), "UTF-8");
                if("buy".equals(action)) {
                    dest = new File(topConfig.getFileBuyGoods() + "/" + fileName);
                    actionName = "进仓单";
                } else if("refund".equals(action)) {
                    dest = new File(topConfig.getFileRefundGoods() + "/" + fileName);
                    actionName = "出仓单";
                } else if("new".equals(action)) {
                    dest = new File(topConfig.getFileNewGoods() + "/" + fileName);
                    actionName = "新建商品单";
                } else if("lock".equals(action)) {
                    dest = new File(topConfig.getFileLockGoods() + "/" + fileName);
                    actionName = "手动锁定库存单";
                } else if("unlock".equals(action)) {
                    dest = new File(topConfig.getFileUnlockGoods() + "/" + fileName);
                    actionName = "手动解锁库存单";
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
                boolean handleResult = action.equals("new")==false? handleExcel(model, dest, action, false) : handleExcel4New(model, dest, false);
                if(!handleResult) {
                    dest.delete();
                    return "goods/upload";
                }
            } catch (IOException e) {
                dest.delete();
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

    private boolean handleExcel(Model model, File file, String action, boolean isDelete) {
        ExcelReader reader = null;
        List<Row> rows = null;
        try {
            reader = new ExcelReader(file);
            if(!isDelete) {
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
            }
            rows = reader.getData();
        } catch (Exception e) {
            log.error("Handle excel failed, ", e);
            model.addAttribute("errorInfo", "系统异常，请联系管理员!");
            return false;
        } finally {
            reader.destroy();
        }

        if(!isDelete) {
            Row row = skuService.checkRow(rows);
            if(null != row) {
                model.addAttribute("errorInfo", "这个商品(" + row.getGoodsId() + "," + row.getColor() + "," + row.getSize() + ")不存在，请检查是否存在错误或者在系统中添加该商品之后重试!");
                return false;
            }
        }
        try {
            skuService.execRow(rows, action, isDelete);
        } catch (InsufficientStockException e) {
            model.addAttribute("errorInfo", "这个商品(" + e.getMessage() + ")库存不足，更新失败!");
            return false;
        } catch (Exception e) {
            model.addAttribute("errorInfo", "系统异常，请重试!");
            return false;
        }
        return true;
    }

    /**
     *
     * @param model
     * @param file
     * @param isDelete, true or false, if false then is add
     * @return
     */
    private boolean handleExcel4New(Model model, File file, boolean isDelete) {
        GoodsExcelReader reader = null;
        List<GoodsRow> rows = null;
        try {
            reader = new GoodsExcelReader(file);
            if(!isDelete) {
                boolean checkColumn = reader.checkColumnName();
                if(!checkColumn) {
                    model.addAttribute("errorInfo", "格式不正确，必须有 编号、名称、价格、颜色编码、颜色、尺码 这几列!");
                    return false;
                }
                int checkData = reader.checkData();
                if(checkData != 0) {
                    model.addAttribute("errorInfo", "第 " + (checkData + 1) + " 行数据格式不正确，请修改后重试!");
                    return false;
                }
            }
            rows = reader.getData();
        } catch (Exception e) {
            log.error("Handle excel failed, ", e);
            model.addAttribute("errorInfo", "系统异常，请联系管理员!");
            return false;
        } finally {
            reader.destroy();
        }

//        String result = goodsService.checkRow(rows);
//        if(null != result) {
//            model.addAttribute("errorInfo", result);
//            return false;
//        }
        try {
            goodsService.execRow(rows, isDelete);
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
        } else if("lock".equals(action)) {
            filePath = new File(topConfig.getFileLockGoods());
        } else if("unlock".equals(action)) {
            filePath = new File(topConfig.getFileUnlockGoods());
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
        } else if("lock".equals(action)) {
            file = new File(topConfig.getFileLockGoods() + "//" + name);
        } else if("unlock".equals(action)) {
            file = new File(topConfig.getFileUnlockGoods() + "//" + name);
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

    @RequestMapping("goods/delete")
    public void delete(Model model, HttpServletResponse resp, @RequestParam("name") String name,
                         @RequestParam("action") String action) {
        File file = null;
        if("buy".equals(action)) {
            file = new File(topConfig.getFileBuyGoods() + "//" + name);
        } else if("new".equals(action)) {
            file = new File(topConfig.getFileNewGoods() + "//" + name);
        } else if ("refund".equals(action)) {
            file = new File(topConfig.getFileRefundGoods() + "//" + name);
        } else if("lock".equals(action)) {
            file = new File(topConfig.getFileLockGoods() + "//" + name);
        } else if("unlock".equals(action)) {
            file = new File(topConfig.getFileUnlockGoods() + "//" + name);
        }
        if(!file.exists()) {
            log.info("File {} not exists, can not be delete!");
            return;
        }
        boolean result = action.equals("new")==false? handleExcel(model, file, action, true) : handleExcel4New(model, file, true);
        if(result) {
            //删除文件
            if(file.exists()) {
                file.delete();
            }
            try {
                resp.sendRedirect("/goods/upload_list?action=" + action);
            } catch (IOException e) {
            }
        } else {
            try {
                resp.sendRedirect("/error");
            } catch(IOException e) {

            }
        }
    }

    @RequestMapping(value="goods/action/change_goods")
    public @ResponseBody ResultInfo changeGoods(Model model, @RequestParam("hid") String hid, @RequestParam("color") String color, @RequestParam("size") String size,
                                                @RequestParam("newValue") String newValue, @RequestParam("oldValue") String oldValue) {
        ResultInfo result = new ResultInfo();
        try {
            MySku sku = skuMapper.select(hid, color, size);
            if(null == sku) {
                result.setErrorInfo("更新失败, 请刷新后重试!");
                result.setSuccess(false);
            }
            long old = Long.parseLong(oldValue);
            long new1 = Long.parseLong(newValue);
            long dValue = new1 - old;
            skuService.updateSku(sku.getId(), dValue, 0, 0, true);

        } catch (Exception e) {
            result.setErrorInfo("更新失败, 请刷新后重试!");
            result.setSuccess(false);
            log.error("更新商品库存失败,", e);
        }
        return result;
    }
}
