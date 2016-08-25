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

        Call<ResponseBody> repo = service.getList();

        String list = getResultFromService(repo);
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
