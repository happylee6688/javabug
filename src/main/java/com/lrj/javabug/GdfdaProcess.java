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
        map.put("entId", "SYNCPKJMD00001026");
        map.put("checkType", "2");
        map.put("start  ", 0);
        map.put("limit", 100);

        params.put("map", map);
        params.put("length", 8);

        json.put("params", params);

        JSONObject context = new JSONObject();
        context.put("javaClass", "HashMap");
        context.put("map", new JSONObject());
        context.put("length", "HashMap");

        json.put("context", context);

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
        Cell cell_4 = headerRow.createCell(2);
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
        map.put("entId", "SYNCPKJMD00001026");
        map.put("start  ", 0);
        map.put("limit", 100);

        params.put("map", map);
        params.put("length", 7);

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
        cell_2.setCellValue("别名");
        Cell cell_3 = headerRow.createCell(2);
        cell_3.setCellValue("英文名");


        Row row;

        for (int i = 0; i < resultArray.size(); i++) {
            JSONObject framInfo = (JSONObject) resultArray.get(i);
            String name = framInfo.getString("NAME");
            String alias = framInfo.getString("ALIAS");
            String engName = framInfo.getString("ENGLISH_NAME");

            //System.out.println("Name: " + name + "; ALIAS: " + alias + "; ENGLISH_NAME: " + engName);

            row = sheet.createRow(i+1);
            Cell c1 = row.createCell(0);
            c1.setCellValue(name);
            Cell c2 = row.createCell(1);
            c2.setCellValue(alias);
            Cell c3 = row.createCell(2);
            c3.setCellValue(engName);


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
                                        .header("Cookie", "route=aed04f74dba81b5b1d110c60bf492663; JSESSIONID=33C67B01CCF48991F87AFBB62DB05338; route=1dfa234d8008b3fe22689b12abe1322b; sso_token=5508D91BB888C1CED406B811CD2414B7")
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
        process.processFram();
    }

}
