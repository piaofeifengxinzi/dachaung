package com.example.demo.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Goods {
    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getNowLocal() {
        return nowLocal;
    }

    public void setNowLocal(String nowLocal) {
        this.nowLocal = nowLocal;
    }

    public String getNextLocal() {
        return nextLocal;
    }

    public void setNextLocal(String nextLocal) {
        this.nextLocal = nextLocal;
    }

    public String getSendpeople() {
        return sendpeople;
    }

    public void setSendpeople(String sendpeople) {
        this.sendpeople = sendpeople;
    }

    public String getReceivepeople() {
        return receivepeople;
    }

    public void setReceivepeople(String receivepeople) {
        this.receivepeople = receivepeople;
    }
    public String getReceivelocal() {
        return receivelocal;
    }

    public void setReceivelocal(String receivelocal) {
        this.receivelocal = receivelocal;
    }

    public String getSendlocal() {
        return sendlocal;
    }

    public void setSendlocal(String sendlocal) {
        this.sendlocal = sendlocal;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    //自增id
    private Integer gid;
    //货物唯一标识符
    private String guid;
    //货物名称
    private String goodsName;
    //货物运输路程，这是一整个字符串，不同的地点在生成货物的时候生成，不同地点用分号隔开
    private String path;
    //当前所在地，这里的数据会让中间快递员知道下一站的信息，末端快递员知道详细信息，没末端也行
    private String nowLocal;
    //当前状态，主要是记录当前货物的状态，同时这里也要记录下一站的信息,(如果没有下一站信息，视为送到）
    private String nextLocal;
    //发货人,存人的uuid
    private String sendpeople;
    //收货人，存人的uuid
    private String receivepeople;
    //收货地
    private String receivelocal;
    //发货地
    private String sendlocal;


}
