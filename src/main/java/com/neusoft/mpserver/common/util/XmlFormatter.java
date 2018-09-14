package com.neusoft.mpserver.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlFormatter {
    private static final String CLMS_COLOR = "blue";
    private static final String DESC_TITLE_COLOR = "blue";
    public enum XmlType{
        CLMS("clms"), DESC("desc");
        private String value;
        XmlType(String value){
            this.value = value;
        }
        public String getValue(){
            return this.value;
        }
    }
    public static String format(String source, XmlType xmlType){
        String result = "";
        if(xmlType.getValue().equals("clms")){
            result = source.replaceAll("<[/]?(patent-document|application-body|claims|claim-text|br|business:Claims|business:ClaimText)[^>]*>", "")
                    .replaceAll("\n", "")
                    .replaceAll("<\\?(xml|trs-parser)[^>]+>", "")
                    .replaceFirst("(<claim|<business:Claim)", "<div style=\"color: " + XmlFormatter.CLMS_COLOR + ";\" ")
                    .replaceAll("(claim|business:Claim)", "div")
                    .replaceAll("<!--[\\w\\W\\r\\n]*?-->", "")
                    .replaceAll("(-|\\+) ", "");
        }else if(xmlType.getValue().equals("desc")){
            result = source.replaceAll("<[/]?(patent-document|application-body|description|drawings|figure|img|br|business:Description|business:Drawings|base:Figure|base:img|base:Bold)[^>]*>", "")
                    .replaceAll("\n", "")
                    .replaceAll("<\\?(xml|trs-parser)[^>]+>", "")
                    .replaceAll("(invention-title|business:InventionTitle)", "h4")
                    .replaceAll("(technical-field|background-art|disclosure|description-of-drawings|mode-for-invention)", "div")
                    .replace(">技术领域", "><span style=\"color: " + XmlFormatter.DESC_TITLE_COLOR + ";\">技术领域</span>")
                    .replace(">背景技术", "><span style=\"color: " + XmlFormatter.DESC_TITLE_COLOR + ";\">背景技术</span>")
                    .replace(">发明内容", "><span style=\"color: " + XmlFormatter.DESC_TITLE_COLOR + ";\">发明内容</span>")
                    .replace(">附图说明", "><span style=\"color: " + XmlFormatter.DESC_TITLE_COLOR + ";\">附图说明</span>")
                    .replace(">具体实施方式", "><span style=\"color: " + XmlFormatter.DESC_TITLE_COLOR + ";\">具体实施方式</span>")
                    .replace("base:Paragraphs", "p")
                    .replaceAll("<!--[\\w\\W\\r\\n]*?-->", "")
                    .replaceAll("(-|\\+) ", "");
        }
        return result;
    }

    public static String  getClms1(String source){
        String result="";
        Pattern p = Pattern.compile("(<div style[^>]+>)([\\w\\W\\r\\n]*?)(</div>)");
        Matcher m = p.matcher(source);
        if(m.find()){
            result = m.group(2);
        }
        return result;
    }
    public static String getPartOfDesc(String source, String part){
        String result = "";
        Pattern p = Pattern.compile("(<p[^>]+>\t*<span[^>]+>"+ part +"</span>\t*</p>\t*<p[^>]+>)([\\w\\W\\r\\n]*?)(</p>)");
        Matcher m = p.matcher(source);
        if(m.find()){
            result = m.group(2);
        }
        return result;
    }
}
