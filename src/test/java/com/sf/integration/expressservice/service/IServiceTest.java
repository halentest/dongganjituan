package com.sf.integration.expressservice.service;

import javax.jws.WebParam;

/**
 * User: hzhang
 * Date: 12/17/13
 * Time: 4:37 PM
 */
public class IServiceTest {
    public static void main(String[] args) throws Exception {
        CommonServiceService service = new CommonServiceService();
        String s = service.getCommonServicePort().sfexpressService(XmlBuilder.get());
        System.out.println(s);
    }
}
