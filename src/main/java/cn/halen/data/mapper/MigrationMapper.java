package cn.halen.data.mapper;

import cn.halen.data.pojo.*;
import cn.halen.data.pojo.migration.Order1;
import cn.halen.data.pojo.migration.Order2;
import cn.halen.data.pojo.migration.Trade1;
import cn.halen.data.pojo.migration.Trade2;
import cn.halen.util.Paging;
import org.apache.commons.lang.StringUtils;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MigrationMapper extends SqlSessionDaoSupport {

    public List<Trade1> selectTrade1() {
        return getSqlSession().selectList("cn.halen.data.mapper.migration.selectTrade1");
    }

    public List<Order1> selectOrder1(String tid) {
        return getSqlSession().selectList("cn.halen.data.mapper.migration.selectOrder1", tid);
    }

    public void insertTrade2(Trade2 t) {
        getSqlSession().insert("cn.halen.data.mapper.migration.insert", t);
    }

    public void insertOrder2(Order2 o) {
        getSqlSession().insert("cn.halen.data.mapper.migration.insertOrder", o);
    }

    public List<String> selectAllTid() {
        return getSqlSession().selectList("cn.halen.data.mapper.migration.selectAllTid");
    }

    public List<MySku> selectAllSku() {
        return getSqlSession().selectList("cn.halen.data.mapper.migration.selectAllSku");
    }

    public long selectGoodsIdByHid(String hid) {

        return getSqlSession().selectOne("cn.halen.data.mapper.migration.selectGoodsIdByHid", hid);
    }

    public void updateGoodsIdLong(long goods_id_long, long id) {
        Map<String, Object> param = new HashMap<String, Object>(2);
        param.put("goods_id_long", goods_id_long);
        param.put("id", id);
        getSqlSession().update("cn.halen.data.mapper.migration.updateGoodsIdLong", param);
    }
}
