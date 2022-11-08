package version4.frame;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.awt.*;

public class Fun {

    private Color color;

    private String F;

    private Object answer;

    private String[] strings=new String[]{"A","C","D",
               "E","F","H","I","J","K","L","M","N","O","P","Q","S","T","U",
               "V","W","Y","Z"};
    private char[]chars=new char[]{'A','C','D',
            'E','F','H','I','J','K','L','M','N','O','P','Q','S','T','U',
            'V','W','Y','Z'};

    public Fun(String f) {
        F = f;
    }

    public String getF() {
        return F;
    }

    public Color getColor() {
        return color;
    }

    public Fun(Color color) {
        this.color = color;
    }


    public void setColor(Color color) {
        this.color = color;
    }

    public Fun() {
        F="X=((G+B)/(2*R)-0.769)/0.271";
    }

    public void setF(String f) {
        F=f;
    }

    public String getAnswer(){
        Binding binding = new Binding();
        binding.setVariable("R",color.getRed());
        binding.setVariable("G",color.getGreen());
        binding.setVariable("B",color.getBlue());
        binding.setVariable("language", "Groovy");
        GroovyShell shell = new GroovyShell(binding);
        if(color.getRed()==0||color.getBlue()==0||color.getGreen()==0)
        {
            return "RGB做分母";
        }

        if(F.length()<3||F.charAt(0)!='X') {
            return "方程错误";
        }
        if(F.charAt(1)!='='){
            return "方程错误";
        }

        for (int i = 0; i < strings.length; i++) {
            if(F.contains(strings[i])){
                return "方程错误";
            }
        }
        answer=shell.evaluate(F);
        return answer.toString();
    }
}
