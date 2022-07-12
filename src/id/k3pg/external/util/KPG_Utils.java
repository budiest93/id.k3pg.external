package id.k3pg.external.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.security.cert.CertificateException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.exceptions.DBException;
import org.adempiere.webui.component.Messagebox;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MAttachment;
import org.compiere.model.MPInstance;
import org.compiere.model.MSysConfig;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Trx;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

public class KPG_Utils {
	protected CLogger log = CLogger.getCLogger(getClass());
    List<Object> params = new ArrayList<>();
    
    public KPG_Utils() {

    }

    public int  getAD_client_ID () {
      return Env.getAD_Client_ID(Env.getCtx());
    }
    
    public static final PreparedStatement setMultiParam(PreparedStatement ps, List<Object> params)
            throws SQLException {
        int index = 0;
        for (Object param : params) {
            index++;
            if (param == null)
                ps.setObject(index, null);
            else if (param instanceof String)
                ps.setString(index, (String) param);
            else if (param instanceof Integer)
                ps.setInt(index, ((Integer) param).intValue());
            else if (param instanceof BigDecimal)
                ps.setBigDecimal(index, (BigDecimal) param);
            else if (param instanceof Timestamp)
                ps.setTimestamp(index, (Timestamp) param);
            else if (param instanceof Boolean)
                ps.setString(index, ((Boolean) param).booleanValue() ? "Y" : "N");
            else if (param instanceof byte[])
                ps.setBytes(index, (byte[]) param);
            else
                throw new DBException("Unknown parameter type " + index + " - " + param);
        }
        return ps;
    }

    public void generateReportList(List<LinkedHashMap<String, Object>> resultList, String trxName,
            String title, String fileName, int ad_p_instance_id) throws IOException {
        int a = 0;
        File file = new File(fileName + ".csv");
        FileWriter fileW = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fileW);
        List<String> listHeader = new ArrayList<>();
        int countFlush = 0;
        for (LinkedHashMap<String, Object> map : resultList) {
            countFlush++;
            a++;
            if (a == 1) {
                bw.write(title);
                bw.newLine();

                for (String key : map.keySet()) {
                    listHeader.add(key);
                }
                createHeaderMap(listHeader, bw);
            }
            for (int j = 0; j < listHeader.size(); ++j) {
                if (map.get(listHeader.get(j)) instanceof String) {
                    bw.write(map.get(listHeader.get(j)) != null
                            ? "\"" + map.get(listHeader.get(j)).toString().replace("\"", "") + "\""
                            : "");
                } else if (map.get(listHeader.get(j)) instanceof BigDecimal) {
                    bw.write(map.get(listHeader.get(j)) != null
                            ? getBigDecimal(map.get(listHeader.get(j)))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP).toString()
                            : Env.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                } else {
                    bw.write(map.get(listHeader.get(j)) != null
                            ? map.get(listHeader.get(j)).toString()
                            : "");
                }

                if (j != listHeader.size() - 1)
                    bw.write(",");
            }
            bw.newLine();
            if (countFlush % 100 == 0) {
                bw.flush();
            }
        }
        bw.flush();
        compressAndAttachIt(file, trxName, ad_p_instance_id);
    }

    public File generateReportSQL(String sql, List<Object> params, String fileName,
        int ad_p_instance_id, Trx localTrx, boolean isDownload) throws Exception {
      PreparedStatement ps = null;
      ResultSet rs = null;
      try {
          ps = DB.prepareStatement(sql, localTrx.getTrxName());
          ps = KPG_Utils.setMultiParam(ps, params);
          if (ps.getConnection().getAutoCommit()) {
              ps.getConnection().setAutoCommit(false);
          }
          ps.setFetchSize(500);
          rs = ps.executeQuery();
          ResultSetMetaData metadata = rs.getMetaData();
          int numColumns = metadata.getColumnCount();
          File file = new File(fileName + ".csv");
          FileWriter fileW = new FileWriter(file);
          BufferedWriter bw = new BufferedWriter(fileW);
          try {
              createHeaderDesc(metadata, numColumns, bw);
              int i = 0;
              while (rs.next()) {
                  ++i;
                  for (int j = 1; j <= numColumns; ++j) {
                      if (rs.getObject(j) instanceof String) {
                          bw.write(rs.getObject(j) != null
                                  ? "\"" + rs.getObject(j).toString().replace("\"", "") + "\""
                                  : "");
                      } else if (rs.getObject(j) instanceof BigDecimal) {
                          bw.write(rs.getObject(j) != null
                                  ? getBigDecimal(rs.getObject(j))
                                          .setScale(2, BigDecimal.ROUND_HALF_UP).toString()
                                  : Env.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                      } else {
                          bw.write(rs.getObject(j) != null ? rs.getObject(j).toString() : "");
                      }
                      if (j != numColumns)
                          bw.write(",");
                  }
                  bw.newLine();
  
                  if (i % 100 == 0)
                      bw.flush();
              }
          } catch (Exception ex) {
              log.log(Level.SEVERE, ex.getMessage());
  
          } finally {
              bw.flush();
              bw.close();
              fileW.close();
              
              if (isDownload) {
                return file;
              } else {
                compressAndAttachIt(file, localTrx.getTrxName(), ad_p_instance_id);
                return null;
              }
          }
      } catch (Exception ex) {
          log.log(Level.SEVERE, ex.getMessage());
          throw new Exception(ex.getMessage());
      } finally {
          if (localTrx.isActive())
              localTrx.commit();
          DB.close(rs, ps);
          ps = null;
          rs = null;
      }
  }
    
    private void compressAndAttachIt(File file, String trxName, int ad_p_instance_id)
            throws IOException {

        byte[] bytes = new byte[1024 * 10];
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(trxName + ".zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zipOut.putNextEntry(zipEntry);
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        zipOut.close();
        fis.close();
        fos.close();

        MAttachment att = new MAttachment(Env.getCtx(), MAttachment.getID(MPInstance.Table_ID, ad_p_instance_id) > 0
                        ? MAttachment.getID(MPInstance.Table_ID, ad_p_instance_id)
                        : 0, trxName);
        if (att.get_ID() == 0) {
            att.setAD_Table_ID(MPInstance.Table_ID);
            att.setRecord_ID(ad_p_instance_id);
        }
        att.addEntry(new File(trxName + ".zip"));
        att.save();

        file.delete();
        File zipDel = new File(trxName + ".zip");
        zipDel.delete();
    }

    private void createHeaderDesc(ResultSetMetaData metadata, int numColumns, BufferedWriter bw)
            throws SQLException, IOException {
        for (int j = 1; j <= numColumns; ++j) {
            String column_name = metadata.getColumnName(j);
            bw.write(column_name);
            if (j != numColumns)
                bw.write(",");
        }
        bw.newLine();
    }

    private void createHeaderMap(List<String> listHeader, BufferedWriter bw) throws IOException {
        for (int j = 0; j < listHeader.size(); ++j) {
            String column_name = listHeader.get(j);
            bw.write(column_name);
            if (j != listHeader.size() - 1)
                bw.write(",");
        }
        bw.newLine();
    }

    public static BigDecimal getBigDecimal(Object value) {
        BigDecimal ret = Env.ZERO;
        if (value != null) {
            if (value instanceof BigDecimal) {
                ret = (BigDecimal) value;
            } else if (value instanceof String) {
                ret = new BigDecimal((String) value);
            } else if (value instanceof BigInteger) {
                ret = new BigDecimal((BigInteger) value);
            } else if (value instanceof Number) {
                ret = new BigDecimal(((Number) value).doubleValue());
            } else if (value instanceof Integer) {
              ret = new BigDecimal(((Integer) value).doubleValue());
          } else {
                throw new ClassCastException("Not possible to coerce [" + value + "] from class "
                        + value.getClass() + " into a BigDecimal.");
            }
        }
        return ret;
    }
    
    public static BigDecimal getBigDecimal(Object value, int scale) {
      return getBigDecimal(value).setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    // 12307
    public static String getStringDate(Timestamp date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date);
    }
    
    public static String getStringDateDmy(Timestamp date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(date);
    }

    public static String getStringPeriod(Timestamp date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        return sdf.format(date);
    }
        
    public ArrayList<String> CompareStrings(String from, String to) {
        ArrayList<String> returnList = new ArrayList<String>();
        int comparison = 0;
        int ascii1 = 0;
        int ascii2 = 0;
        int maxLength = 0;
        from = from.trim().toUpperCase();
        to = to.trim().toUpperCase();
        String c = " ";

        if (from.length() > to.length()) {
            maxLength = from.length();
            for (int j = 0; j <= (maxLength - to.length()); j++) {
                to += c;
            }
        } else if (from.length() < to.length()) {
            maxLength = to.length();
            for (int j = 0; j <= (maxLength - from.length()); j++) {
                from += c;
            }
        } else {
            maxLength = from.length();
        }

        for (int i = 0; i < maxLength; i++) {
            ascii1 = (int) from.charAt(i);
            ascii2 = (int) to.charAt(i);
            comparison = ascii1 - ascii2;

            if (comparison != 0) {
                if (ascii1 > ascii2) {
                    returnList.add(to.trim());
                    returnList.add(from.trim());
                    break;
                } else if (ascii1 < ascii2) {
                    returnList.add(from.trim());
                    returnList.add(to.trim());
                    break;
                }
            } else {
                returnList.add(from.trim());
                returnList.add(from.trim());
            }

        }
        return returnList;
    }

    public List<Object> getListSingleObjectFromSQL(StringBuilder sql, List<Object> params){
        List<Object> listObject = new ArrayList<>(); 
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = DB.prepareStatement(sql.toString(), null);
            ps = setMultiParam(ps, params);
            ps.setFetchSize(100);
            rs = ps.executeQuery();
            while (rs.next()) {
                listObject.add(rs.getObject(1));
            }
        } catch (Exception e) {
            throw new AdempiereException(e.getMessage());
        } finally {
            DB.close(rs, ps);
            ps = null;
            rs = null;
        }
        return listObject;
    }
    
    public List<Object> getListObjectFromSQL(String sql,List<Object> params) {
        List<Object> listObject = new ArrayList<>(); 
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = DB.prepareStatement(sql,null);
            DB.setParameters(ps, params);
            ps.setFetchSize(100);
            rs = ps.executeQuery();
            ResultSetMetaData metadata = rs.getMetaData();
            int countColumn = metadata.getColumnCount();
            while (rs.next()) {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                for (int a=1; a<=countColumn; a++) {
                    map.put(metadata.getColumnName(a), rs.getObject(metadata.getColumnName(a)));
                }
                listObject.add(map);
            }
        } catch (Exception e) {
            throw new AdempiereException(e.getMessage());
        } finally {
            DB.close(rs, ps);
            ps = null;
            rs = null;
        }
        return listObject;
    }
    
    public List<Object> getListObjectFromSQL(String sql,List<Object> params, String trxName) {
      List<Object> listObject = new ArrayList<>(); 
      PreparedStatement ps = null;
      ResultSet rs = null;
      try {
          ps = DB.prepareStatement(sql,trxName);
          DB.setParameters(ps, params);
          ps.setFetchSize(100);
          rs = ps.executeQuery();
          ResultSetMetaData metadata = rs.getMetaData();
          int countColumn = metadata.getColumnCount();
          while (rs.next()) {
              LinkedHashMap<String, Object> map = new LinkedHashMap<>();
              for (int a=1; a<=countColumn; a++) {
                  map.put(metadata.getColumnName(a), rs.getObject(metadata.getColumnName(a)));
              }
              listObject.add(map);
          }
      } catch (Exception e) {
          throw new AdempiereException(e.getMessage());
      } finally {
          DB.close(rs, ps);
          ps = null;
          rs = null;
      }
      return listObject;
  }
    
    public List<Integer> getListIntegerFromSQL(String sql,List<Object> params) {
      List<Integer> listData = new ArrayList<>(); 
      try {
          for (LinkedHashMap<String, Object> map: getListMapFromSQL(sql, params)) {
            int countKey = 0;
            for (String key: map.keySet()) {
              if (countKey > 1) {
                throw new Exception("Key of map must be 1!");
              }
              countKey+=1;
              if (!(map.get(key) instanceof Integer)) {
                throw new Exception("Value is not Integer!");
              }
              listData.add((int)map.get(key));
            }
          }
      } catch (Exception e) {
          throw new AdempiereException(e.getMessage());
      }
      return listData;
    }
    
    public String getWhereInFromListInteger(String sql,List<Object> params) {
      return convertListIntegerToWhereIn(getListIntegerFromSQL(sql, params));
    }
    
    public List<String> getListStringFromSQL(String sql,List<Object> params) {
      List<String> listData = new ArrayList<>(); 
      try {
          for (LinkedHashMap<String, Object> map: getListMapFromSQL(sql, params)) {
            int countKey = 0;
            for (String key: map.keySet()) {
              if (countKey > 1) {
                throw new Exception("Key of map must be 1!");
              }
              countKey+=1;
              if (!(map.get(key) instanceof String)) {
                throw new Exception("Value is not String!");
              }
              listData.add((String)map.get(key));
            }
          }
      } catch (Exception e) {
          throw new AdempiereException(e.getMessage());
      }
      return listData;
    }
    
    public String getWhereInFromListString(String sql,List<Object> params) {
      return convertListStringToWhereIn(getListStringFromSQL(sql, params));
    }
    
    public LinkedHashMap<String, Object> getMapFromSQL(String sql,List<Object> params){
        List<Object> listObject = getListObjectFromSQL(sql, params);
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        if (listObject.size() > 0) {
            map = (LinkedHashMap<String, Object>) listObject.get(0);
        }
        return map;
    }
    
    public LinkedHashMap<String, Object> getMapFromSQL(String sql,List<Object> params, String trxName){
      List<Object> listObject = getListObjectFromSQL(sql, params, trxName);
      LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
      if (listObject.size() > 0) {
          map = (LinkedHashMap<String, Object>) listObject.get(0);
      }
      return map;
  }
    
    public List<LinkedHashMap<String, Object>> getListMapFromSQL(String sql,List<Object> params){
        List<LinkedHashMap<String, Object>> resultList = new ArrayList<LinkedHashMap<String,Object>>();
        List<Object> listObject = new ArrayList<Object>();
        
        if(!sql.equals("")) {
            listObject = getListObjectFromSQL(sql, params);
        }
        
        for(Object o : listObject) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
            map = (LinkedHashMap<String, Object>) o;
            resultList.add(map);
        }
        return resultList;
    }
    
    public List<LinkedHashMap<String, Object>> getListMapFromSQL(String sql,List<Object> params, String trxName){
      List<LinkedHashMap<String, Object>> resultList = new ArrayList<LinkedHashMap<String,Object>>();
      List<Object> listObject = new ArrayList<Object>();
      
      if(!sql.equals("")) {
          listObject = getListObjectFromSQL(sql, params, trxName);
      }
      
      for(Object o : listObject) {
          LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
          map = (LinkedHashMap<String, Object>) o;
          resultList.add(map);
      }
      return resultList;
  }
    
    public boolean isEmptyString(String str) {
        if (str == null || "".equalsIgnoreCase(str)) {
            return true;
        }
        return false;
    }
    
    public String requestHTTP(String requestMethod, String url, Object o,
            Map<String, String> mapHeader) {
        String result = "";
        OkHttpClient client = new OkHttpClient();
        StringBuilder uri = new StringBuilder();

        if (o != null) {
            if (o instanceof Map) {
                try {
                    Map<String, String> map = (Map<String, String>) o;
                    int count = 0;
                    for (String key : map.keySet()) {
                        if (count > 0) {
                            uri.append("&");
                        }
                        String value = map.get(key);
                        if (mapHeader.get("Content-Type") != null
                                && ((String) mapHeader.get("Content-Type"))
                                        .equalsIgnoreCase("application/x-www-form-urlencoded")) {
                            value = URLEncoder.encode(value, "UTF-8");
                        }
                        uri.append(key).append("=").append(value);
                        count++;
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new AdempiereException(e.getMessage());
                }
            } else if (o instanceof JSONObject) {
                JSONObject jo = (JSONObject) o;
                uri.append(jo.toString());
            }
        }

        MediaType mediaType = null;
        if (mapHeader.get("Content-Type") != null) {
            if (((String) mapHeader.get("Content-Type")).equalsIgnoreCase("application/json")) {
                mediaType = MediaType.parse("application/json");
            } else if (((String) mapHeader.get("Content-Type"))
                    .equalsIgnoreCase("application/x-www-form-urlencoded")) {
                mediaType = MediaType.parse("application/x-www-form-urlencoded");
            }
        } else {
            if (uri.length() > 0) {
                url = url + "?" + uri.toString();
            }
        }

        RequestBody body = null;
        if (uri.length() > 0) {
            body = RequestBody.create(mediaType, uri.toString());
        }

        Builder rb = new Request.Builder().url(url);
        if (requestMethod.equalsIgnoreCase("POST")) {
            rb.post(body);
        } else if (requestMethod.equalsIgnoreCase("GET")) {
            rb.get();
        } else if (requestMethod.equalsIgnoreCase("PUT")) {
            rb.put(body);
        }
        for (String key : mapHeader.keySet()) {
            rb.addHeader(key, mapHeader.get(key));
        }
        Request request = rb.build();
        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            throw new AdempiereException(e.getMessage());
        }
        return result;
    }
    
    public String requestHTTPSUntrusted(String requestMethod, String url, Object o,
            Map<String, String> mapHeader) throws Exception {
        String result = "";
        OkHttpClient client = getUnsafeOkHttpClient();
        StringBuilder uri = new StringBuilder();

        if (o != null) {
            if (o instanceof Map) {
                try {
                    Map<String, String> map = (Map<String, String>) o;
                    int count = 0;
                    for (String key : map.keySet()) {
                        if (count > 0) {
                            uri.append("&");
                        }
                        String value = map.get(key);
                        if (mapHeader.get("Content-Type") != null
                                && ((String) mapHeader.get("Content-Type"))
                                        .equalsIgnoreCase("application/x-www-form-urlencoded")) {
                            value = URLEncoder.encode(value, "UTF-8");
                        }
                        uri.append(key).append("=").append(value);
                        count++;
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new AdempiereException(e.getMessage());
                }
            } else if (o instanceof JSONObject) {
                JSONObject jo = (JSONObject) o;
                uri.append(jo.toString());
            }
        }

        MediaType mediaType = null;
        if (mapHeader.get("Content-Type") != null) {
            if (((String) mapHeader.get("Content-Type")).equalsIgnoreCase("application/json")) {
                mediaType = MediaType.parse("application/json");
            } else if (((String) mapHeader.get("Content-Type"))
                    .equalsIgnoreCase("application/x-www-form-urlencoded")) {
                mediaType = MediaType.parse("application/x-www-form-urlencoded");
            }
        } else {
            if (uri.length() > 0) {
                url = url + "?" + uri.toString();
            }
        }

        RequestBody body = null;
        if (uri.length() > 0) {
            body = RequestBody.create(mediaType, uri.toString());
        }

        Builder rb = new Request.Builder().url(url);
        if (requestMethod.equalsIgnoreCase("POST")) {
            rb.post(body);
        } else if (requestMethod.equalsIgnoreCase("GET")) {
            rb.get();
        } else if (requestMethod.equalsIgnoreCase("PUT")) {
            rb.put(body);
        }
        for (String key : mapHeader.keySet()) {
            rb.addHeader(key, mapHeader.get(key));
        }
        Request request = rb.build();

        try {
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
                throw new Exception(response.message());
            }
            result = response.body().string();
        } catch (IOException e) {
            throw new AdempiereException(e.getMessage());
        }
        return result;
    }
    
    public static String requestHTTPMultipart(String requestMethod, String url, Object o,
            Map<String, String> mapHeader, File file) throws Exception {
        String result = "";
        OkHttpClient client = new OkHttpClient();
        StringBuilder uri = new StringBuilder();

        if (o != null) {
            if (o instanceof Map) {
                try {
                    Map<String, String> map = (Map<String, String>) o;
                    int count = 0;
                    for (String key : map.keySet()) {
                        if (count > 0) {
                            uri.append("&");
                        }
                        String value = map.get(key);
                        if (mapHeader.get("Content-Type") != null
                                && ((String) mapHeader.get("Content-Type"))
                                        .equalsIgnoreCase("application/x-www-form-urlencoded")) {
                            value = URLEncoder.encode(value, "UTF-8");
                        }
                        uri.append(key).append("=").append(value);
                        count++;
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new Exception(e.getMessage());
                }
            } else if (o instanceof JSONObject) {
                JSONObject jo = (JSONObject) o;
                uri.append(jo.toString());
            }
        }

        MediaType mediaType = null;
        if (mapHeader.get("Content-Type") != null) {
            if (((String) mapHeader.get("Content-Type")).equalsIgnoreCase("application/json")) {
                mediaType = MediaType.parse("application/json");
            } else if (((String) mapHeader.get("Content-Type"))
                    .equalsIgnoreCase("application/x-www-form-urlencoded")) {
                mediaType = MediaType.parse("application/x-www-form-urlencoded");
            }
        } else {
            if (uri.length() > 0) {
                url = url + "?" + uri.toString();
            }
        }

        RequestBody body = null;
        okhttp3.MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
        multipartBuilder.setType(MultipartBody.FORM);
        if (uri.length() > 0) {
            multipartBuilder.addPart(RequestBody.create(mediaType, uri.toString()));
        }
        if (file != null) {
            multipartBuilder.addFormDataPart("file", file.getName(),
                    (RequestBody.create(MediaType.parse("application/octet-stream"), file)));
        }
        if (uri.length() > 0 || file != null) {
            body = multipartBuilder.build();
        }

        Builder rb = new Request.Builder().url(url);
        if (requestMethod.equalsIgnoreCase("POST")) {
            rb.post(body);
        } else if (requestMethod.equalsIgnoreCase("GET")) {
            rb.get();
        } else if (requestMethod.equalsIgnoreCase("PUT")) {
            rb.put(body);
        }
        for (String key : mapHeader.keySet()) {
            rb.addHeader(key, mapHeader.get(key));
        }
        Request request = rb.build();
        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
        return result;
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {}

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {}

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[] {};
                }
            }};

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            //timeout
            builder.connectTimeout(70, TimeUnit.SECONDS);
            builder.writeTimeout(70, TimeUnit.SECONDS);
            builder.readTimeout(70, TimeUnit.SECONDS);
            
            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public String formatRupiah(BigDecimal amt) {
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);
        return kursIndonesia.format(amt);
    }
    
    public void sendSMS(String msg, Integer c_bpartner_id) throws Exception {
        try {
            //@formatter:off
            String sql = 
                    "SELECT " + 
                    "    u.phone " + 
                    "FROM c_bpartner bp " + 
                    "INNER JOIN ad_user u ON u.c_bpartner_id = bp.c_bpartner_id " + 
                    "WHERE bp.c_bpartner_id = ? " + 
                    "FETCH FIRST 1 ROWS ONLY ";
            //@formatter:on
            params.clear();
            params.add(c_bpartner_id);
            String phone = DB.getSQLValueStringEx(null, sql, params);
            if (!isEmptyString(phone)) {
                String smsEmail = MSysConfig.getValue("");
                String smsToken = MSysConfig.getValue("");
                Map<String, String> mapParam = new HashMap<>();
                Map<String, String> mapHeader = new HashMap<>();
                String url = "https://masking.medansms.co.id/sms_api.php?action=kirim_sms"
                    +"&email="+smsEmail
                    +"&passkey="+smsToken
                    +"&no_tujuan="+phone
                    +"&pesan="+msg
                    +"&json=1";
                requestHTTPSUntrusted("GET", url, mapParam, mapHeader);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    
    public void sendWA(String msg, String header, String footer, Integer c_bpartner_id) throws Exception {
        try {
            //@formatter:off
            String sql = 
                    "SELECT " + 
                    "    u.forca_nowa " + 
                    "FROM c_bpartner bp " + 
                    "INNER JOIN ad_user u ON u.c_bpartner_id = bp.c_bpartner_id " + 
                    "WHERE bp.c_bpartner_id = ? " + 
                    "FETCH FIRST 1 ROWS ONLY ";
            //@formatter:on
            params.clear();
            params.add(c_bpartner_id);
            String phone = DB.getSQLValueStringEx(null, sql, params);
            sendWAOrigin(msg, header, footer, phone);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    
    public void sendWAOrigin(String msg, String header, String footer, String phone) throws Exception {
        try {
            if (!isEmptyString(phone) ) {
                Map<String, String> mapHeader = new HashMap<>();
                mapHeader.put("Content-Type", "application/json");
                mapHeader.put("Authorization", "Bearer "+MSysConfig.getValue(""));
                JSONObject joHeader = new JSONObject();
                joHeader.put("type", "text");
                joHeader.put("text", header);
                JSONObject jo = new JSONObject();
                jo.put("to", phone);
                jo.put("type", "text");
                jo.put("text", msg);
                jo.put("header", joHeader);
                jo.put("footer", footer);
                String url = "https://client.multy.chat/"+MSysConfig.getValue("")+"/api/messages/push";
                requestHTTPSUntrusted("POST", url, jo, mapHeader);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    
    public String nullToString (String str) {
        if (str == null) {
            return "";
        }
        return str;
    }
    
    public String addZero(int str, int total) {
        return String.format("%0"+total+"d", str);
    }
    
    public Integer getDefaultOrg() {
    	String sql = 
    			"SELECT " + 
    			"    ad_org_id::int " + 
    			"FROM ad_org " + 
    			"WHERE ad_client_id = ? " + 
    			"AND name = 'K3PG' " + 
    			"FETCH FIRST 1 ROWS ONLY ";
    	return DB.getSQLValueEx(null, sql, Env.getAD_Client_ID(Env.getCtx()));
    }
    
    public Integer getTaxByName(String name) {
    	String sql = 
    			"SELECT " + 
    			"    c_tax_id::int " + 
    			"FROM c_tax " + 
    			"WHERE ad_client_id = ? " + 
    			"AND name = ? " + 
    			"FETCH FIRST 1 ROWS ONLY ";
    	return DB.getSQLValueEx(null, sql, Env.getAD_Client_ID(Env.getCtx()), name);
    }
    
    public Integer getDocTypeByName(String name) {
    	String sql = 
    			"SELECT " + 
    			"    c_doctype_id::int " + 
    			"FROM c_doctype " + 
    			"WHERE ad_client_id = ? " + 
    			"AND name = ? " + 
    			"FETCH FIRST 1 ROWS ONLY ";
    	return DB.getSQLValueEx(null, sql, Env.getAD_Client_ID(Env.getCtx()), name);
    }
    
    public Timestamp getCurrentTime() {
      return Env.getContextAsDate(Env.getCtx(), "#Date") != null
          ? Env.getContextAsDate(Env.getCtx(), "#Date")
          : new Timestamp((new Date().getTime()));
    }
    
    public LinkedHashMap<String, Object> getBPDetail(int c_bpartner_id){
        List<Object> params = new ArrayList<>();
        //@formatter:off
        String sql =
                "SELECT " + 
                "    bp.c_bpartner_id::int, " + 
                "    bpls.c_bpartner_location_id::int shipto_id, " + 
                "    bplb.c_bpartner_location_id::int billto_id, " + 
                "    u.ad_user_id::int " + 
                "FROM c_bpartner bp " + 
                "LEFT JOIN ( " + 
                "    SELECT " + 
                "        bpl.c_bpartner_id, " + 
                "        bpl.c_bpartner_location_id " + 
                "    FROM c_bpartner_location bpl " + 
                "    WHERE bpl.c_bpartner_id = ? " + 
                "    AND bpl.isshipto = 'Y' " + 
                "    ORDER BY created DESC " + 
                "    FETCH FIRST 1 ROWS ONLY " + 
                ") bpls ON bpls.c_bpartner_id = bp.c_bpartner_id " + 
                "LEFT JOIN ( " + 
                "    SELECT " + 
                "        bpl.c_bpartner_id, " + 
                "        bpl.c_bpartner_location_id " + 
                "    FROM c_bpartner_location bpl " + 
                "    WHERE bpl.c_bpartner_id = ? " + 
                "    AND bpl.isbillto = 'Y' " + 
                "    ORDER BY created DESC " + 
                "    FETCH FIRST 1 ROWS ONLY " + 
                ") bplb ON bplb.c_bpartner_id = bp.c_bpartner_id " + 
                "LEFT JOIN ( " + 
                "    SELECT " + 
                "        u.c_bpartner_id, " + 
                "        u.ad_user_id " + 
                "    FROM ad_user u " + 
                "    WHERE u.isactive = 'Y' " + 
                "    AND u.c_bpartner_id = ? " + 
                "    ORDER BY created DESC " + 
                "    FETCH FIRST 1 ROWS ONLY " + 
                ") u ON u.c_bpartner_id = bp.c_bpartner_id " +
                "WHERE bp.c_bpartner_id = ? ";
        //@formatter:on
        params.add(c_bpartner_id);
        params.add(c_bpartner_id);
        params.add(c_bpartner_id);
        params.add(c_bpartner_id);
        return getMapFromSQL(sql, params);
    }
    
    public MAcctSchema getDefaultAcctSchema(int ad_client_id) {
        List<Object> params = new ArrayList<>();
        //@formatter:off
        String sql = 
                "SELECT " + 
                "    cas.c_acctschema_id " + 
                "FROM c_acctschema cas " + 
                "WHERE cas.isactive = 'Y' " + 
                "AND cas.ad_client_id = ? " + 
                "ORDER BY created DESC " + 
                "FETCH FIRST 1 ROWS ONLY ";
        //@formatter:on
        params.add(ad_client_id);
        int c_acctschema_id = DB.getSQLValueEx(null, sql, params);
        MAcctSchema result = new MAcctSchema(Env.getCtx(), c_acctschema_id, null);
        return result;
    }
    
    public String convertListToWhereIn(List<Object> list) {
        StringBuilder sb = new StringBuilder();
        for (int a = 0; a < list.size(); a++) {
            if (a > 0) {
                sb.append(", ");
            }
            if (list.get(a) instanceof Integer) {
                sb.append(String.valueOf((Integer)list.get(a)));
            } else if (list.get(a) instanceof String) {
                sb.append("'");
                sb.append((String)list.get(a));
                sb.append("'");
            }
        }
        return sb.toString();
    }
    
    public String convertListIntegerToWhereIn(List<Integer> list) {
        StringBuilder sb = new StringBuilder();
        for (int a = 0; a < list.size(); a++) {
            if (a > 0) {
                sb.append(", ");
            }
            sb.append(String.valueOf((Integer)list.get(a)));
        }
        return sb.toString();
    }
    
    public String convertListStringToWhereIn(List<String> list) {
      StringBuilder sb = new StringBuilder();
      for (int a = 0; a < list.size(); a++) {
          if (a > 0) {
              sb.append(", ");
          }
          sb.append("'");
          sb.append((String)list.get(a));
          sb.append("'");
      }
      return sb.toString();
  }
    
    public List<Object> convertJsonArrayToList(JSONArray ja) {
        List<Object> list = new ArrayList<>();
        try {
            for (int a=0; a < ja.length(); a++) {
                JSONObject jo = ja.getJSONObject(a);
                LinkedHashMap<String, Object> map1 = convertJsonObjectToMap(jo);
                list.add(map1);
            }
        } catch (JSONException e) {
        }
        return list;
    }
    
    public LinkedHashMap<String, Object> convertJsonObjectToMap(JSONObject jo) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        try {
            Iterator<String> results = jo.keys();
            while (results.hasNext()) {
                String key = results.next();
                Object jRes = jo.get(key);
                if (jRes instanceof JSONArray) {
                    map.put(key,convertJsonArrayToList((JSONArray)jRes));
                } else {
                    map.put(key, jo.get(key));
                }
            }
        } catch (JSONException e) {
        }
        return map;
    }
    
    public String getRandomStr(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
          .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
          .limit(length)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          .toString();

        return generatedString;
    }
    
    public String generateRandomNumber(int total) {
        return generateRandomChars("0123456789", total);
    }
    
    public String generateRandomChars(String candidateChars, int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(candidateChars.charAt(random.nextInt(candidateChars
                    .length())));
        }

        return sb.toString();
    }
    
    public String addSpace(String str, int total) {
        StringBuilder result = new StringBuilder();
        int lStr = str.length();
        for(int a = 0; a<total; a++) {
            if (a < lStr) {
                result.append(str.substring(a, a+1));
            } else {
                result.append(" ");
            }
        }
        return result.toString();
    }
    
    public List<String> getListBase64Attachment(int ad_table_id, int record_id) {
        List<String> listBinary = new ArrayList<>();
        MAttachment attach = MAttachment.get(Env.getCtx(), ad_table_id, record_id);
        if (attach != null) {
            byte[] data = attach.getBinaryData();
            if (data != null) {
                try {
                    ByteArrayInputStream in = new ByteArrayInputStream(data);
                    ZipInputStream zip = new ZipInputStream (in);
                    ZipEntry entry = zip.getNextEntry();
                    while (entry != null)
                    {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        byte[] buffer = new byte[2048];
                        int length = zip.read(buffer);
                        while (length != -1)
                        {
                            out.write(buffer, 0, length);
                            length = zip.read(buffer);
                        }
                        byte[] dataEntry = out.toByteArray();
                        listBinary.add(Base64.getEncoder().encodeToString(dataEntry));
                        entry = zip.getNextEntry();
                    }
                } catch (IOException e) {
                    throw new AdempiereException(e.getMessage());
                }
            }
        }
        return listBinary;
    }
    
    public String getBase64Attachment(int ad_table_id, int record_id) {
        String binary = "";
        MAttachment attach = MAttachment.get(Env.getCtx(), ad_table_id, record_id);
        if (attach != null) {
            byte[] data = attach.getBinaryData();
            if (data != null) {
                try {
                    ByteArrayInputStream in = new ByteArrayInputStream(data);
                    ZipInputStream zip = new ZipInputStream (in);
                    ZipEntry entry = zip.getNextEntry();
                    while (entry != null)
                    {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        byte[] buffer = new byte[2048];
                        int length = zip.read(buffer);
                        while (length != -1)
                        {
                            out.write(buffer, 0, length);
                            length = zip.read(buffer);
                        }
                        byte[] dataEntry = out.toByteArray();
                        binary = Base64.getEncoder().encodeToString(dataEntry);
//                        entry = zip.getNextEntry();
                        entry = null;
                    }
                } catch (IOException e) {
                    throw new AdempiereException(e.getMessage());
                }
            }
        }
        return binary;
    }
    
    public String getStringDate(Object date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
    
    public int getDocType(String docbasetype, String name) {
        List<Object> params = new ArrayList<>();
        //@formatter:off
        String sql = 
            "SELECT " + 
            "    c_doctype_id " + 
            "FROM c_doctype " + 
            "WHERE ad_client_id = ? " + 
            "AND docbasetype = ? " + 
            "AND name = ? ";
        //@formatter:on
        params.clear();
        params.add(Env.getAD_Client_ID(Env.getCtx()));
        params.add(docbasetype);
        params.add(name);
        return DB.getSQLValueEx(null, sql, params);
    }
    
    public int getCurrency(String iso_code) {
        List<Object> params = new ArrayList<>();
        //@formatter:off
        String sql = 
            "SELECT " + 
            "    c_currency_id " + 
            "FROM c_currency " + 
            "WHERE iso_code = ? " + 
            "FETCH FIRST 1 ROWS ONLY ";
        //@formatter:on
        params.add(iso_code);
        return DB.getSQLValueEx(null, sql, params);
    }
    
    public String getCurrencyISO(int c_currency_id) {
        List<Object> params = new ArrayList<>();
        //@formatter:off
        String sql = 
            "SELECT " + 
            "    iso_code " + 
            "FROM c_currency " + 
            "WHERE c_currency_id = ? " + 
            "FETCH FIRST 1 ROWS ONLY ";
        //@formatter:on
        params.add(c_currency_id);
        return DB.getSQLValueStringEx(null, sql, params);
    }
    
    public int getFirstPeriod() {
    	String sql = 
			"SELECT " + 
			"    c_period_id " + 
			"FROM c_period " + 
			"WHERE ad_client_id = ? " + 
			"AND to_char(( " + 
			"    SELECT " + 
			"    min(dateacct) " + 
			"FROM fact_acct  " + 
			"WHERE ad_client_id = ? " + 
			"), 'YYYYMMDD') BETWEEN to_char(startdate,'YYYYMMDD') AND to_char(enddate,'YYYYMMDD') ";
 		params.clear();
 		params.add(Env.getAD_Client_ID(Env.getCtx()));
 		params.add(Env.getAD_Client_ID(Env.getCtx()));
 		int c_period_id = DB.getSQLValueEx(null, sql, params);
 		
 		return c_period_id;
    }
    
    public List<List<LinkedHashMap<String, Object>>> getSplitThreadData(int p_TotalThread, List<LinkedHashMap<String, Object>> listData) {
      int countMultiThreading = p_TotalThread;
      int tempCountAccount = 0;
      int splitter = listData.size() / countMultiThreading;
      
      List<List<LinkedHashMap<String, Object>>> listResult = new ArrayList<>();
      List<LinkedHashMap<String, Object>> listTemp = new ArrayList<>();
      for (LinkedHashMap<String, Object> mapData: listData) {
        if (tempCountAccount > splitter || tempCountAccount == 0) {
          tempCountAccount = 0;
          listTemp = new ArrayList<>();
          listResult.add(listTemp);
        }
        listResult.get(listResult.size()-1).add(mapData);
        tempCountAccount += 1;
      }
      
      return listResult;
    }
    
    public List<List<String>> getSplitThreadDataString(int p_TotalThread, List<String> listData) {
      int countMultiThreading = p_TotalThread;
      int tempCountAccount = 0;
      int splitter = listData.size() / countMultiThreading;
      
      List<List<String>> listResult = new ArrayList<>();
      List<String> listTemp = new ArrayList<>();
      for (String data: listData) {
        if (tempCountAccount > splitter || tempCountAccount == 0) {
          tempCountAccount = 0;
          listTemp = new ArrayList<>();
          listResult.add(listTemp);
        }
        listResult.get(listResult.size()-1).add(data);
        tempCountAccount += 1;
      }
      
      return listResult;
    }
    
    public List<List<Integer>> getSplitThreadDataInt(int p_TotalThread, List<Integer> listData) {
      int countMultiThreading = p_TotalThread;
      int tempCountAccount = 0;
      int splitter = listData.size() / countMultiThreading;
      
      List<List<Integer>> listResult = new ArrayList<>();
      List<Integer> listTemp = new ArrayList<>();
      for (int data: listData) {
        if (tempCountAccount > splitter || tempCountAccount == 0) {
          tempCountAccount = 0;
          listTemp = new ArrayList<>();
          listResult.add(listTemp);
        }
        listResult.get(listResult.size()-1).add(data);
        tempCountAccount += 1;
      }
      
      return listResult;
    }
    
    public int getColumnID (String tablename, String columnname) {
      List<Object> params = new ArrayList<>();
      params.add(tablename);
      params.add(columnname);
      return DB.getSQLValueEx(null,
          "select " + 
          "    coalesce(c.ad_column_id, 0)::int " + 
          "from ad_column c " + 
          "inner join ad_table t on t.ad_table_id = c.ad_table_id " + 
          "where t.tablename = ? " + 
          "and c.columnname = ? ", params);
    }
    
    public void encryptPDF(File file, String pass) {
      try {
        PDDocument document;
        document = PDDocument.load(file);
        AccessPermission ap = new AccessPermission();         
        StandardProtectionPolicy spp = new StandardProtectionPolicy(pass, pass, ap);
        spp.setEncryptionKeyLength(128);
        spp.setPermissions(ap);
        document.protect(spp);
        document.save(file.getPath());
        document.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    public void showMsgDialog(String msg) {
      Messagebox.showDialog(msg, "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
    }
    
    public String execCURL(String[] command) {
        String result = "";
        ProcessBuilder process = new ProcessBuilder(command); 
        Process p;
        try
        {
            p = process.start();
             BufferedReader reader =  new BufferedReader(new InputStreamReader(p.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ( (line = reader.readLine()) != null) {
                        builder.append(line);
                        builder.append(System.getProperty("line.separator"));
                }
                result = builder.toString();

        }
        catch (IOException e)
        {   System.out.print("error");
            e.printStackTrace();
        }
        return result;
    }
    
    //getClientInfo
    ///////////////////////////////////////////////////
    public void printClientInfo(HttpServletRequest request) {
      final String referer = getReferer(request);
      final String fullURL = getFullURL(request);
      final String clientIpAddr = getClientIpAddr(request);
      final String clientOS = getClientOS(request);
      final String clientBrowser = getClientBrowser(request);
      final String userAgent = getUserAgent(request);
      
      log.info("\n" +
                          "User Agent \t" + userAgent + "\n" +
                          "Operating System\t" + clientOS + "\n" +
                          "Browser Name\t" + clientBrowser + "\n" +
                          "IP Address\t" + clientIpAddr + "\n" +
                          "Full URL\t" + fullURL + "\n" +
                          "Referrer\t" + referer);
    }

    public String getReferer(HttpServletRequest request) {
        final String referer = request.getHeader("referer");
        return referer;
    }
  
    public String getFullURL(HttpServletRequest request) {
        final StringBuffer requestURL = request.getRequestURL();
        final String queryString = request.getQueryString();
  
        final String result = queryString == null ? requestURL.toString() : requestURL.append('?')
                .append(queryString)
                .toString();
  
        return result;
    }
  
    public String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
  
    public String getClientOS(HttpServletRequest request) {
        final String browserDetails = request.getHeader("User-Agent");
  
        //=================OS=======================
        final String lowerCaseBrowser = browserDetails.toLowerCase();
        if (lowerCaseBrowser.contains("windows")) {
            return "Windows";
        } else if (lowerCaseBrowser.contains("mac")) {
            return "Mac";
        } else if (lowerCaseBrowser.contains("x11")) {
            return "Unix";
        } else if (lowerCaseBrowser.contains("android")) {
            return "Android";
        } else if (lowerCaseBrowser.contains("iphone")) {
            return "IPhone";
        } else {
            return "UnKnown, More-Info: " + browserDetails;
        }
    }
  
    public String getClientBrowser(HttpServletRequest request) {
        final String browserDetails = request.getHeader("User-Agent");
        final String user = browserDetails.toLowerCase();
  
        String browser = "";
  
        //===============Browser===========================
        if (user.contains("msie")) {
            String substring = browserDetails.substring(browserDetails.indexOf("MSIE")).split(";")[0];
            browser = substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1];
        } else if (user.contains("safari") && user.contains("version")) {
            browser = (browserDetails.substring(browserDetails.indexOf("Safari")).split(" ")[0]).split(
                    "/")[0] + "-" + (browserDetails.substring(
                    browserDetails.indexOf("Version")).split(" ")[0]).split("/")[1];
        } else if (user.contains("opr") || user.contains("opera")) {
            if (user.contains("opera"))
                browser = (browserDetails.substring(browserDetails.indexOf("Opera")).split(" ")[0]).split(
                        "/")[0] + "-" + (browserDetails.substring(
                        browserDetails.indexOf("Version")).split(" ")[0]).split("/")[1];
            else if (user.contains("opr"))
                browser = ((browserDetails.substring(browserDetails.indexOf("OPR")).split(" ")[0]).replace("/",
                                                                                                           "-")).replace(
                        "OPR", "Opera");
        } else if (user.contains("chrome")) {
            browser = (browserDetails.substring(browserDetails.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        } else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1) || (user.indexOf(
                "mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1) || (user.indexOf(
                "mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1)) {
            //browser=(userAgent.substring(userAgent.indexOf("MSIE")).split(" ")[0]).replace("/", "-");
            browser = "Netscape-?";
  
        } else if (user.contains("firefox")) {
            browser = (browserDetails.substring(browserDetails.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        } else if (user.contains("rv")) {
            browser = "IE";
        } else {
            browser = "UnKnown, More-Info: " + browserDetails;
        }
  
        return browser;
    }
  
    public String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
    ////////////////////////////////////////////////////////////////
    
    public List<Integer> getSelectionIDFromInfoWindow(int ad_pinstance_id){
      params.clear();
      //@formatter:off
      String sql =
          "select " + 
          "    t_selection_id::int " + 
          "from t_selection " + 
          "where ad_pinstance_id = ? ";
      //@formatter:on
      params.add(ad_pinstance_id);
      return getListIntegerFromSQL(sql, params);
    }
    
    public byte[] compressImage(byte[] file, Float size) {
    	byte[] jpegData = null;
    	if (size == null) {
    		size = 0.5f;
    	}
    	try {
    		ByteArrayOutputStream compressed = new ByteArrayOutputStream();
            ImageOutputStream outputStream = ImageIO.createImageOutputStream(compressed);
			outputStream = ImageIO.createImageOutputStream(compressed);
			ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
	
	        ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
	        jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	        jpgWriteParam.setCompressionQuality(size);
	        jpgWriter.setOutput(outputStream);
	        jpgWriter.write(null, new IIOImage(createImageFromBytes(file), null, null),
	                jpgWriteParam);
	        jpgWriter.dispose();
	        jpegData = compressed.toByteArray();
	        } catch (IOException e) {
				new AdempiereException(e.getMessage());
			}

        return jpegData;
    }

    private BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return removeAlphaChannel(ImageIO.read(bais));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static BufferedImage removeAlphaChannel(BufferedImage img) {
        if (!img.getColorModel().hasAlpha()) {
            return img;
        }

        BufferedImage target = createImage(img.getWidth(), img.getHeight(), false);
        Graphics2D g = target.createGraphics();
        // g.setColor(new Color(color, false));
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return target;
    }
    private static BufferedImage createImage(int width, int height, boolean hasAlpha) {
        return new BufferedImage(width, height, hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
    }
    
}
