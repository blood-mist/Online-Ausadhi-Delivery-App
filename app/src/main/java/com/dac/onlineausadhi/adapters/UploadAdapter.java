package com.dac.onlineausadhi.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

public class UploadAdapter {
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;
    StringBuffer stringBuffer;
    StringBuilder sbParams;
    URL url;

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @param requestURL
     * @param charset
     * @throws IOException
     */
    public UploadAdapter(String requestURL, String charset, String access_token, String method) {
        this.charset = charset;

        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";

        switch (method) {
            case "GET":
                sbParams = new StringBuilder();
                // request method is GET
                if (sbParams.length() != 0) {
                    requestURL += "?" + sbParams.toString();
                }

                try {
                    url = new URL(requestURL);
                    httpConn = (HttpURLConnection) url.openConnection();
                    httpConn.setDoOutput(false);
                    httpConn.setRequestMethod("GET");
                    httpConn.setRequestProperty("Accept-Charset", charset);
                    httpConn.setRequestProperty("Authorization", "Bearer " + access_token);
                    httpConn.setConnectTimeout(15000);
                    httpConn.connect();
                } catch (UnknownHostException | MalformedURLException f) {
                    f.printStackTrace();
                } catch (ProtocolException p) {
                    p.printStackTrace();
                } catch (IOException i) {
                    i.printStackTrace();
                }
                break;
            case "POST":
                try {
                    url = new URL(requestURL);
                    httpConn = (HttpURLConnection) url.openConnection();
                    httpConn.setUseCaches(false);
                    httpConn.setChunkedStreamingMode(1024);
                    httpConn.setRequestMethod("POST");
                    httpConn.setDoOutput(true); // indicates POST method
                    httpConn.setDoInput(true);
                    httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                    httpConn.setRequestProperty("Authorization", "Bearer " + access_token);
                    outputStream = httpConn.getOutputStream();
                    writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
                } catch (IOException i) {
                    i.printStackTrace();
                }
                break;
            case "PUT":
                try {
                    url = new URL(requestURL);
                    httpConn = (HttpURLConnection) url.openConnection();
                    httpConn.setDoOutput(false);
                    httpConn.setRequestMethod("PUT");
                    httpConn.setRequestProperty("Accept-Charset", charset);
                    httpConn.setRequestProperty("Authorization", "Bearer " + access_token);
                    httpConn.setConnectTimeout(15000);
                    httpConn.connect();
                } catch (IOException i) {
                    i.printStackTrace();
                }
                break;
            case "DELETE":
                try {
                    url = new URL(requestURL);
                    httpConn = (HttpURLConnection) url.openConnection();
                    httpConn.setRequestMethod("DELETE");
                    httpConn.setRequestProperty("Accept-Charset", charset);
                    httpConn.setRequestProperty("Authorization", "Bearer " + access_token);
                    httpConn.setConnectTimeout(15000);
                    httpConn.connect();
                } catch (IOException i) {
                    i.printStackTrace();
                }
                break;
        }
    }

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public void addFields(String[]name,String value) throws NullPointerException, JSONException {
        JSONObject send=new JSONObject();
        for(int i=0; i<name.length;i++){
            send.put("\""+name[i]+"\"",name[i]);

        }


    }
    public void addFormField(String name, String value) throws NullPointerException {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile) throws IOException {
        String fileName = uploadFile.getName();

        int rotate = 0;
        try {
            File imageFile = new File(uploadFile.getPath());
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Matrix matrix = new Matrix();

        matrix.postRotate(rotate);

        Bitmap scaledBitmap = decodeFile(uploadFile.getPath(), 720, 650, ScalingLogic.FIT);

        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos);
        InputStream in = new ByteArrayInputStream(bos.toByteArray());

        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        //FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        in.close();

        writer.append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a header field to the request.
     *
     * @param name  - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Completes the request and receives response from the server.
     *
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public String finish(String method) throws IOException {
        if (method.equals("POST")) {
            //writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();
        }

        // checks server's status code first
        int status;
        try {
            // Will throw IOException if server responds with 401 for old devices
            status = httpConn.getResponseCode();
        } catch (IOException e) {
            // Will return 401, because now connection has the correct internal state.
            status = httpConn.getResponseCode();
        }

       Log.v("status", "" + status);
        BufferedReader reader;
        if (status == HttpURLConnection.HTTP_OK) {
            reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream()));
        }

        String inputLine;
        stringBuffer = new StringBuffer();

        while ((inputLine = reader.readLine()) != null) {
            stringBuffer.append(inputLine);
        }
        reader.close();
        httpConn.disconnect();

        return stringBuffer.toString();
    }

    public static Bitmap decodeFile(String pathName, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth, dstHeight, scalingLogic);

        return BitmapFactory.decodeFile(pathName, options);
    }

    public static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                return srcWidth / dstWidth;
            } else {
                return srcHeight / dstHeight;
            }
        } else {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                return srcHeight / dstHeight;
            } else {
                return srcWidth / dstWidth;
            }
        }
    }

    public enum ScalingLogic {
        CROP, FIT
    }
}