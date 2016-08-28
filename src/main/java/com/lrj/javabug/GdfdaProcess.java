package com.lrj.javabug;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lrj.javabug.service.GdfdaService;
import okhttp3.*;
import okhttp3.Response;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import retrofit2.*;
import retrofit2.Call;

import java.io.FileOutputStream;
import java.io.IOException;


/**
 *
 * Created by Administrator on 2016/8/22.
 */
public class GdfdaProcess {

    /**
     * 获取检测结果的list
     */
    public void process() {

        GdfdaService service = getService();

        JSONObject json = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("javaClass", "ParameterSet");

        JSONObject map = new JSONObject();
        map.put("needTotal", true);
        map.put("sampleQDeptId", "SYNCPKJMD00001026");
        map.put("checkType", "2");
        map.put("start  ", 0);
        map.put("limit", 3);

        params.put("map", map);
        params.put("length", 8);

        json.put("params", params);

        JSONObject context = new JSONObject();
        context.put("javaClass", "HashMap");
        context.put("map", new JSONObject());
        context.put("length", 0);

        json.put("context", context);

        System.out.println(json.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), json.toString());
        Call<ResponseBody> repo = service.getList(body);



        String list = getResultFromService(repo);

        JSONObject resultJson = JSONObject.parseObject(list);
        JSONArray resultArray = resultJson.getJSONArray("rows");

        //处理excel结果
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("快速检测单");
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(12.75f);
        Cell cell_1 = headerRow.createCell(0);
        cell_1.setCellValue("样品名称");
        Cell cell_2 = headerRow.createCell(1);
        cell_2.setCellValue("抽样日期");
        Cell cell_3 = headerRow.createCell(2);
        cell_3.setCellValue("抽样经手人");
        Cell cell_4 = headerRow.createCell(3);
        cell_4.setCellValue("被抽样经营者");
        Cell cell_5 = headerRow.createCell(4);
        cell_5.setCellValue("样标来源（省）");
        Cell cell_6 = headerRow.createCell(5);
        cell_6.setCellValue("样标来源（市）");
        Cell cell_7 = headerRow.createCell(6);
        cell_7.setCellValue("样标来源（县/区）");
        Cell cell_8 = headerRow.createCell(7);
        cell_8.setCellValue("样标来源（镇/街道）");
        Cell cell_9 = headerRow.createCell(8);
        cell_9.setCellValue("进货日期");
        Cell cell_10 = headerRow.createCell(9);
        cell_10.setCellValue("进货数量（kg）");
        Cell cell_11 = headerRow.createCell(10);
        cell_11.setCellValue("抽样数量（kg）");
        Cell cell_12 = headerRow.createCell(11);
        cell_12.setCellValue("供货者名称");
        Cell cell_13 = headerRow.createCell(12);
        cell_13.setCellValue("供货者地址");
        Cell cell_14 = headerRow.createCell(13);
        cell_14.setCellValue("生产者名称");
        Cell cell_15 = headerRow.createCell(14);
        cell_15.setCellValue("生产者地址");
        Cell cell_16 = headerRow.createCell(15);
        cell_16.setCellValue("检测总结论");
        Cell cell_17 = headerRow.createCell(16);
        cell_17.setCellValue("检测时间");
        Cell cell_18 = headerRow.createCell(17);
        cell_18.setCellValue("检测项目");
        Cell cell_19 = headerRow.createCell(18);
        cell_19.setCellValue("检测类型");
        Cell cell_20 = headerRow.createCell(19);
        cell_20.setCellValue("标准值");
        Cell cell_21 = headerRow.createCell(20);
        cell_21.setCellValue("计量单位");
        Cell cell_22 = headerRow.createCell(21);
        cell_22.setCellValue("检测值");
        Cell cell_23 = headerRow.createCell(22);
        cell_23.setCellValue("检测结论");
        Cell cell_24 = headerRow.createCell(23);
        cell_24.setCellValue("备注");

        Row row;

        for (int i = 0; i < resultArray.size() ; i++) {
            //拿出检测结果的概览信息
            JSONObject info = (JSONObject) resultArray.get(i);

            //样品id
            String id = info.getString("ID");

            //抽样详情
            JSONObject detail = (JSONObject) getetailInfo(id, "ID@=", service, 1).get(0);

            //抽烟检测详情
            JSONObject check = (JSONObject) getetailInfo(id, "id", service, 2).get(0);

            //省市县代码
            String addressCode = info.getString("PRODUCT_AREA_CODE");     //省市县代码
            String[] addr = getAddr(addressCode, service);


            JSONArray provinceJson = getetailInfo(addressCode, "superCode", service, 3);  //来源省份的json
            JSONArray cityJson = getetailInfo(addressCode, "superCode", service, 3);      //来源市的json
            JSONArray countyJson = getetailInfo(addressCode, "superCode", service, 3);    //来源县的json


            String sampleName = info.getString("SAMPLE_Q_NAME");   //样品名称
            String sampleDate = info.getString("SAMPLE_DATE");     //抽样日期
            String sampleDeptPerson = info.getString("SAMPLE_DEPT_PERSON");     //抽样经手人
            String operatorName = info.getString("OPERATOR_NAME");       //被抽样经营者

            String province = addr[0];       //样标来源（省）
            String city = addr[1];       //样标来源（市）
            String county = addr[2];       //样标来源（县/区）
            String twon = "";                                   //样标来源（镇/街道）

            String supplierDate = detail.getString("supplierDate");          //进货日期
            String supplierNum = detail.getString("supplierNum");          //进货数量（kg）
            String sampleNum = detail.getString("sampleQNum");          //抽样数量（kg）
            String supplierName = detail.getString("supplierName");          //供货者名称

            String supplierAddress = detail.getString("supplierAddress");      //供货者地址
            String producerName = detail.getString("producerName");      //生产者名称
            String producerAddress = detail.getString("producerAddress");      //生产者地址
            String checkResult = info.getString("SAMPLE_Q_CHECK_RES").equals("1") ? "合格" : "不合格";      //检测总结论

            String checkDate = detail.getString("sampleDate");      //检测时间
            String inspectionItem = check.getString("inspectionItem");      //检测项目
            String checkType = "";                                  //检测类型
            String limitAmount = check.getString("limitAmount");      //标准值

            String inspectionUnit = check.getString("inspectionUnit");      //计量单位
            String inspectionResult = check.getString("inspectionResult");      //检测值
            String inspectionResultJudge = check.getString("inspectionResultJudge").equals("0") ? "合格" : "不合格";      //检测结论
            String remark = "";                                  //备注


            row = sheet.createRow(i+1);

            Cell c1 = row.createCell(0);
            c1.setCellValue(sampleName);
            Cell c2 = row.createCell(1);
            c2.setCellValue(sampleDate);
            Cell c3 = row.createCell(2);
            c3.setCellValue(sampleDeptPerson);
            Cell c4 = row.createCell(3);
            c4.setCellValue(operatorName);

            Cell c5 = row.createCell(4);
            c5.setCellValue(province);
            Cell c6 = row.createCell(5);
            c6.setCellValue(city);
            Cell c7 = row.createCell(6);
            c7.setCellValue(county);
            Cell c8 = row.createCell(7);
            c8.setCellValue(twon);

            Cell c9 = row.createCell(8);
            c9.setCellValue(supplierDate);
            Cell c10 = row.createCell(9);
            c10.setCellValue(supplierNum);
            Cell c11 = row.createCell(10);
            c11.setCellValue(sampleNum);
            Cell c12 = row.createCell(11);
            c12.setCellValue(supplierName);

            Cell c13 = row.createCell(12);
            c13.setCellValue(supplierAddress);
            Cell c14 = row.createCell(13);
            c14.setCellValue(producerName);
            Cell c15 = row.createCell(14);
            c15.setCellValue(producerAddress);
            Cell c16 = row.createCell(15);
            c16.setCellValue(checkResult);

            Cell c17 = row.createCell(16);
            c17.setCellValue(checkDate);
            Cell c18 = row.createCell(17);
            c18.setCellValue(inspectionItem);
            Cell c19 = row.createCell(18);
            c19.setCellValue(checkType);
            Cell c20 = row.createCell(19);
            c20.setCellValue(limitAmount);

            Cell c21 = row.createCell(20);
            c21.setCellValue(inspectionUnit);
            Cell c22 = row.createCell(21);
            c22.setCellValue(inspectionResult);
            Cell c23 = row.createCell(22);
            c23.setCellValue(inspectionResultJudge);
            Cell c24 = row.createCell(23);
            c24.setCellValue(remark);

            int rowNum = i + 1;
            System.out.println("成功保存了第" + rowNum +  "行");

            String file = "e:\\checkList.xls";

            try {
                FileOutputStream out = new FileOutputStream(file);
                wb.write(out);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    /**
     * 样品详细信息
     * @param id  样品id
     * @return    json对象
     * @param service    retrofit的service
     * @param urlType    要查询的url，对应service的方法:
     *                   1:getDetail
     *                   2:getCheck
     *                   3:getAddress
     * @return
     */
    public JSONArray getetailInfo(String id, String key, GdfdaService service, int urlType) {


        JSONObject json = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("javaClass", "ParameterSet");

        JSONObject map = new JSONObject();
        //把key和id存进post中
        map.put(key, id);

        params.put("map", map);
        params.put("length", 1);

        json.put("params", params);

        JSONObject context = new JSONObject();
        context.put("javaClass", "HashMap");
        context.put("map", new JSONObject());
        context.put("length", 0);

        json.put("context", context);

        //System.out.println(json.toString());

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), json.toString());
        Call<ResponseBody> repo;

        switch (urlType){
            case 1:
                repo = service.getDetail(body);
                break;
            case 2:
                repo = service.getCheck(body);
                break;
            case 3:
                repo = service.getAddress(body);
                break;
            default:
                repo = service.getDetail(body);
        }

        String list = getResultFromService(repo);

        JSONObject resultJson = JSONObject.parseObject(list);
        JSONArray resultArray = resultJson.getJSONArray("rows");

        return resultArray;
    }


    /**
     * 根据所在地区的代码，查询省、市、县的名称
     * @param code     代码
     * @param service  retrofit的service
     * @return         省市县的数组，0存放省、1存放市、2存放县
     */
    public String[] getAddr(String code, GdfdaService service) {

        String stateCode = "CN";                              //国家级代码
        String provinceCode = code.substring(0,2) + "0000";   //省级代码
        String cityCode = code.substring(0,4) + "00";         //市级代码

        String[] addr = new String[3];

        JSONArray provinceArray = getetailInfo(stateCode, "superCode", service, 3);    //使用国家级代码查找有多少个省
        JSONArray cityeArray = getetailInfo(provinceCode, "superCode", service, 3);    //使用省级代码查找有多少个市
        JSONArray countyArray = getetailInfo(cityCode, "superCode", service, 3);       //使用市级代码查找有多少个县

        for (int i = 0; i < provinceArray.size(); i++) {
            JSONObject res = (JSONObject) provinceArray.get(i);
            if(res.getString("cantCode").equals(provinceCode)) {
                addr[0] = res.getString("cantName");
            }
        }

        for (int i = 0; i < cityeArray.size(); i++) {
            JSONObject res = (JSONObject) cityeArray.get(i);
            if(res.getString("cantCode").equals(cityCode)) {
                addr[1] = res.getString("cantName");
            }
        }

        for (int i = 0; i < countyArray.size(); i++) {
            JSONObject res = (JSONObject) countyArray.get(i);
            if(res.getString("cantCode").equals(cityCode)) {
                addr[2] = res.getString("cantName");
            }
        }

        return addr;
    }

    /**
     * 获取农产品的list
     */
    public void processFram() {
        GdfdaService service = getService();

        JSONObject json = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("javaClass", "ParameterSet");

        JSONObject map = new JSONObject();
        map.put("needTotal", true);
        map.put("classId", "");
        map.put("className", "");
        map.put("anotherName", "");
        map.put("entId", "SYNCPKJMD00001026");
        map.put("start  ", 0);
        map.put("limit", 2700);

        params.put("map", map);
        params.put("length", 11);

        json.put("params", params);

        JSONObject context = new JSONObject();
        context.put("javaClass", "HashMap");
        context.put("map", new JSONObject());
        context.put("length", "HashMap");

        json.put("context", context);

        //System.out.println(json.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), json.toString());


        Call<ResponseBody> repo = service.getFramList(body);

        String result = getResultFromService(repo);

        JSONObject resultJson = JSONObject.parseObject(result);
        JSONArray resultArray = resultJson.getJSONArray("rows");


        //处理excel结果
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("常用农产品");
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(12.75f);
        Cell cell_1 = headerRow.createCell(0);
        cell_1.setCellValue("中文名");
        Cell cell_2 = headerRow.createCell(1);
        cell_2.setCellValue("产品代码");
        Cell cell_3 = headerRow.createCell(2);
        cell_3.setCellValue("父id");
        Cell cell_4 = headerRow.createCell(3);
        cell_4.setCellValue("别名");
        Cell cell_5 = headerRow.createCell(4);
        cell_5.setCellValue("英文名");


        Row row;

        for (int i = 0; i < resultArray.size(); i++) {
            JSONObject framInfo = (JSONObject) resultArray.get(i);
            String name = framInfo.getString("NAME");
            String productCode = framInfo.getString("PRODUCT_CODE");
            String parentId = framInfo.getString("PARENT_ID");
            String alias = framInfo.getString("ALIAS");
            String engName = framInfo.getString("ENGLISH_NAME");

            //System.out.println("Name: " + name + "; ALIAS: " + alias + "; ENGLISH_NAME: " + engName);

            row = sheet.createRow(i+1);
            Cell c1 = row.createCell(0);
            c1.setCellValue(name);
            Cell c2 = row.createCell(1);
            c2.setCellValue(productCode);
            Cell c3 = row.createCell(2);
            c3.setCellValue(parentId);
            Cell c4 = row.createCell(3);
            c4.setCellValue(alias);
            Cell c5 = row.createCell(4);
            c5.setCellValue(engName);

            int rowNum = i + 1;
            System.out.println("成功保存了第" + rowNum +  "行; NAME" + name + "; ALIAS: " + alias + "; ENGLISH_NAME: " + engName);
        }

        String file = "e:\\framList.xls";

        try {
            FileOutputStream out = new FileOutputStream(file);
            wb.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println(result);
    }






    /**
     * 通过一个响应体，执行同步请求，把结果转换成String
     * @param repo
     * @return
     */
    public String getResultFromService(Call<ResponseBody> repo) {
        String result = "";
        try {
            retrofit2.Response<ResponseBody> bodyResponse  = repo.execute();
            result = bodyResponse.body().string();
            //System.out.println(text);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public GdfdaService getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://e.gdfda.gov.cn/")
                .client(getOKHttpClient())
                .build();

        GdfdaService service = retrofit.create(GdfdaService.class);
        return  service;
    }

    /**
     * 设置okhttp拦截器，通过拦截器设置cookie以及content-type等http请求头
     * @return
     */
    public OkHttpClient getOKHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Interceptor.Chain chain) throws IOException {
                                Request original = chain.request();

                                // Request customization: add request headers
                                Request.Builder requestBuilder = original.newBuilder()
                                        //现在pc端登录，通过开发者工具获取头部信息
                                        .header("Cookie", "route=f6a7ce89c0bfb5356431342ae6b6a981; route=92064b68ffe839f20877e6b9d4944903; JSESSIONID=4B37C11FD6085868F52327B6A5773AEE; route=15d455410c575d87f3a9113772b9f0c1; sso_token=469E2F5539F7919A826EEDE5061A9AAE")
                                        .header("Content-Type", "application/json")
                                        .method(original.method(), original.body());

                                Request request = requestBuilder.build();
                                return chain.proceed(request);
                            }
                        })
                .build();
    }


    public static void main(String[] args) {
        GdfdaProcess process = new GdfdaProcess();
        process.process();

//        GdfdaService service = process.getService();
//        JSONArray array = process.getetailInfo("8aa851b856a5dabb0156d18ebea34788", "id", service, 2);
//
//        System.out.println("");

    }

}
