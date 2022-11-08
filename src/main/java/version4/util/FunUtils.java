package version4.util;



import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

public class FunUtils {
    private String F;

    private String key;

/*    static {
        //加载配置文件，如果没有，则先创建一个配置文件
        prop = new Properties();
        try {
            File properties = new File(PATH);
            if (!properties.exists()) {
                //文件不存在，生成
                boolean newFile = properties.createNewFile();
            }
            FileInputStream fis = new FileInputStream(properties);
            prop.load(fis);
        } catch (IOException e) {
        }
    }*/
    public void writeProper() {

        try {
            Properties properties = new Properties();
            properties.setProperty("FUN", F);
            OutputStream outputStream = new FileOutputStream(new File("global.properties"));
            properties.store(outputStream, "方程文件");
            outputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String readProper() throws IOException {

        Properties properties = new Properties();
        //InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("global.properties");//获取配置文件输入流
        FileInputStream inputStream = new FileInputStream(new File("global.properties"));
        properties.load(inputStream);//载入输入流
        F=properties.getProperty("FUN");
        inputStream.close();
        return F;
    }


    public String getF() {
        return F;
    }

    public void setF(String f) {
        F = f;
    }
}
