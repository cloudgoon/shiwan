/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.weixin.entity;


import com.jeeplus.core.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;

/**
 * 微信模板Entity
 *
 * @author toteny
 * @version 2018-06-03
 */
public class WxTplMsgText extends DataEntity<WxTplMsgText> {

    private static final long serialVersionUID = 1L;
    private String tplId;        // 模板ID
    private String title;        // 消息标题
    private String content;        // 消息内容
    private String wxTpl;        // 微信模板
    private String baseId;        // 消息主表id
    private String account;        // account
    private WxMsgBase wxMsgBase;


    public WxTplMsgText() {
        super();
    }

    public WxTplMsgText(String id) {
        super(id);
    }

    @ExcelField(title = "模板ID", align = 2, sort = 1)
    public String getTplId() {
        return tplId;
    }

    public void setTplId(String tplId) {
        this.tplId = tplId;
    }

    @ExcelField(title = "消息标题", align = 2, sort = 2)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ExcelField(title = "消息内容", align = 2, sort = 3)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @ExcelField(title = "微信模板", align = 2, sort = 4)
    public String getWxTpl() {
        return wxTpl;
    }

    public void setWxTpl(String wxTpl) {
        this.wxTpl = wxTpl;
    }

    @ExcelField(title = "消息主表id", align = 2, sort = 5)
    public String getBaseId() {
        return baseId;
    }

    public void setBaseId(String baseId) {
        this.baseId = baseId;
    }

    @ExcelField(title = "account", align = 2, sort = 6)
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public WxMsgBase getWxMsgBase() {
        return wxMsgBase;
    }

    public void setWxMsgBase(WxMsgBase wxMsgBase) {
        this.wxMsgBase = wxMsgBase;
    }
}