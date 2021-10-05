package com.example.Controller;

import com.example.Entity.Handle;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author: 倪路
 * Time: 2021/10/5-9:50
 * StuNo: 1910400731
 * Class: 19104221
 * Description:
 */
@RestController
public class HandleController {
    private Map<String, Set<String>> expressions = new HashMap<>();  // 存储所有产生式
    private Set<String> VT = new HashSet<>();    // 存储终结符
    private Set<String> VN = new HashSet<>();    // 存储非终结符
    private Map<String, Set<String>> FIRST = new HashMap<>();    // 存储FIRST集
    private Map<String, Set<String>> FOLLOW = new HashMap<>();   // 存储FOLLOW集
    private Map<String, Set<String>> RELATION = new HashMap<>(); // 存储依赖关系，即更新了一个另一个也需要一起更新
    private Map<String, Set<String>> FIRSTVT = new HashMap<>();  // 存储FIRSTVT集
    private Map<String, Set<String>> LASTVT = new HashMap<>();   // 存储LASTVT集
    private Map<String,Integer> S2I = new HashMap<>();           // 存储最后的算符优先表的字符到行列的映射关系
    private String[][] VTTABLE = null;  // 存储算符优先关系表
    private Map<String, Set<String>> LINK = new HashMap<>();
    private String begin = "";
    private boolean flag = true;
    private boolean flag1 = true;

    private boolean check(){
        for (String s : VN) {
            for (String s1 : LINK.get(s)) {
                if(LINK.get(s1).contains(s))
                    return false;
            }
        }
        return true;
    }

    private void handleInput(int num , String input){
        String[] expression = new String [num];
        String[] temp = input.split("\n");
        for(int i=0;i<num;i++) expression[i] = temp[i];
        begin=""+(expression[0].charAt(0));
        for(int i=0;i<num;i++){
            String[] s1 = expression[i].split(" ");
            VN.add(s1[0]);
            if(expressions.containsKey(s1[0])){
                expressions.get(s1[0]).add(expression[i].substring(5));
            }else{
                expressions.put(s1[0], new HashSet<>());
                expressions.get(s1[0]).add(expression[i].substring(5));
            }
        }
        VN.forEach((s)->{LINK.put(s,new HashSet<>());});
        for(int i=0;i<num;i++){
            String[] s1 = expression[i].split(" ");
            if(VN.contains(s1[2])){
                LINK.get(s1[2]).add(s1[0]);
                LINK.get(s1[2]).addAll(LINK.get(s1[0]));
            }
        }
        if(!check()){
            flag = false;
        }
        for(int i=0;i<num;i++){
            String[] s = expression[i].split(" ");
            for(int j=2;j<s.length;j++){
                if(j>2&&VN.contains(s[j])&&VN.contains(s[j-1])) flag1=false;
                if(VN.contains(s[j]))   continue;
                VT.add(s[j]);
            }
        }
    }

    private  Set<String> handleFirst(String cur){
        Set<String> res = new HashSet<>();
        if(VT.contains(cur)){
            res.add(cur);
            return res;
        }
        expressions.get(cur).forEach((s)->{
            String[] s1 = s.split(" ");
            boolean flag = true;
            int i=0;
            while(flag && i<s1.length) {
                if (VT.contains(s1[i])) {
                    res.add(s1[i]);
                    flag=false;
                } else {
                    Set<String> temp = handleFirst(s1[i]);
                    res.addAll(temp);
                    if(!temp.contains("#"))  flag = false;
                    else    i++;
                }
            }
        });
        return res;
    }

    private  void updateAll(String cur){
        RELATION.get(cur).forEach((s)->{
            int pre_size = FOLLOW.get(s).size();
            FOLLOW.get(s).addAll(FOLLOW.get(cur));
            if(FOLLOW.get(s).size()>pre_size){
                updateAll(s);
            }
        });
    }

    private  void handleFollow(String cur){
        expressions.get(cur).forEach((s)->{
            String[] s1 = s.split(" ");
            for(int i=0;i<s1.length;i++) {
                if (VT.contains(s1[i])) continue;
                int pre_size = FOLLOW.get(s1[i]).size();
                // A -->  ...aB
                if (i == s1.length - 1) {
                    FOLLOW.get(s1[i]).addAll(FOLLOW.get(cur));
                    if(FOLLOW.get(s1[i]).size()>pre_size){
                        updateAll((s1[i]));
                    }
                    RELATION.get(cur).add(s1[i]);
                }
                // A -->  ...aBc...
                else if(VT.contains(s1[i+1])){
                    FOLLOW.get(s1[i]).add(s1[i+1]);
                    if(FOLLOW.get(s1[i]).size()>pre_size){
                        updateAll((s1[i]));
                    }
                }
                // A --> ...aBC...
                else{
                    FOLLOW.get(s1[i]).addAll(FIRST.get(s1[i+1]));
                    if(FIRST.get(s1[i+1]).contains("#")){
                        FOLLOW.get(s1[i]).addAll(FOLLOW.get(s1[i+1]));
                        RELATION.get(s1[i+1]).add(s1[i]);
                    }
                    if(FOLLOW.get(s1[i]).size()>pre_size){
                        updateAll((s1[i]));
                    }
                }
            }
        });
    }

    public  Set<String> handleFirstVT(String cur){
        Set<String> res =new HashSet<>();
        expressions.get(cur).forEach((s)->{
            String[] s1 = s.split(" ");
            // T  -> a....
            if(VT.contains(s1[0])){
                res.add(s1[0]);
            }
            // T -> Ra...
            else if(s1.length>1&&VT.contains(s1[1])){
                if(!s1[0].equals(cur))
                    res.addAll(handleFirstVT(s1[0]));
                res.add(s1[1]);
            }
            // T -> R...
            else{
                if(!s1[0].equals(cur))
                    res.addAll(handleFirstVT(s1[0]));
            }
        });
        return res;
    }

    public  Set<String> handleLastVT(String cur){
        Set<String> res =new HashSet<>();
        expressions.get(cur).forEach((s)->{
            String[] s1 = s.split(" ");
            int i = s1.length-1;
            // T -> ...a
            if(VT.contains(s1[i])){
                res.add(s1[i]);
            }
            // T -> ...aR
            else if(s1.length>1&&VT.contains(s1[i-1])){
                if(!s1[i].equals(cur))
                    res.addAll(handleLastVT(s1[i]));
                res.add(s1[i-1]);
            }
            // T -> ...R
            else{
                if(!s1[i].equals(cur))
                    res.addAll(handleLastVT(s1[i]));
            }
        });
        return res;
    }

    public void handleVtTable(){
        expressions.forEach((k,v)->{
            for (String s : v) {
                String[] s1 = s.split(" ");
                for(int i=0;i<s1.length;i++){
                    if(VT.contains(s1[i])){
                        if(i>0&&VN.contains(s1[i-1])){
                            if(LASTVT.get(s1[i - 1])!=null)
                                for (String s2 : LASTVT.get(s1[i - 1])) {
                                    VTTABLE[S2I.get(s2)][S2I.get(s1[i])]=">";
                                }
                        }
                        if(i<s1.length-1){
                            if(VT.contains(s1[i+1]))
                                VTTABLE[S2I.get(s1[i])][S2I.get(s1[i+1])]="=";
                            else if(i<s1.length-2&&VT.contains(s1[i+2])){
                                VTTABLE[S2I.get(s1[i])][S2I.get(s1[i+2])]="=";
                            }
                            if(FIRSTVT.get(s1[i + 1])!=null)
                                for (String s2 : FIRSTVT.get(s1[i + 1])) {
                                    VTTABLE[S2I.get(s1[i])][S2I.get(s2)]="<";
                                }
                        }
                    }
                }
            }
        });
    }

    public void soutTable(StringBuilder sb){
        Formatter formatter = new Formatter(sb);
        for (int j = 0 ; j < VT.size()*5 ; j++ ){
            sb.append("-");
        }
        sb.append("算符优先表");
        for (int j = 0 ; j < VT.size()*5 ; j++ ){
            sb.append("-");
        }
        sb.append("\n");
        for(String[] cur : VTTABLE){
            for(String s: cur){
                if(s!=null)
                    formatter.format("%-8s",s);
                else
                    formatter.format("%-8s","#");
                formatter.format("%-5s","|");
            }
            sb.append("\n");
            for (int j = 0 ; j < VT.size()*11 ; j++ ){
                sb.append("-");
            }
            sb.append("\n");
        }
    }

    public void soutMap(String msg,Map<String,Set<String>> map,StringBuilder sb){
        for (int j = 0 ; j < VT.size()*4 ; j++ ){
            sb.append("-");
        }
        sb.append(msg);
        for (int j = 0 ; j < VT.size()*4 ; j++ ){
            sb.append("-");
        }
        sb.append("\n");
        map.forEach((k,v)->{
            sb.append(k+" : ");
            sb.append(v+"\n");
            sb.append("\n");
        });
    }

    public void soutSet(String msg,Set<String> set,StringBuilder sb){
        for (int j = 0 ; j < VT.size()*4 ; j++ ){
            sb.append("-");
        }
        sb.append(msg);
        for (int j = 0 ; j < VT.size()*4 ; j++ ){
            sb.append("-");
        }
        sb.append("\n");
        set.forEach((s)->{
            sb.append(s+" ");
        });
        sb.append("\n");
    }

    private void destroy(){
        expressions = new HashMap<>();  // 存储所有产生式
        VT = new HashSet<>();    // 存储终结符
        VN = new HashSet<>();    // 存储非终结符
        FIRST = new HashMap<>();    // 存储FIRST集
        FOLLOW = new HashMap<>();   // 存储FOLLOW集
        RELATION = new HashMap<>(); // 存储依赖关系，即更新了一个另一个也需要一起更新
        FIRSTVT = new HashMap<>();  // 存储FIRSTVT集
        LASTVT = new HashMap<>();   // 存储LASTVT集
        S2I = new HashMap<>();           // 存储最后的算符优先表的字符到行列的映射关系
        VTTABLE = null;  // 存储算符优先关系表
        begin = "";
        LINK=new HashMap<>();
    }

    @PostMapping("handle")
    public String handler(@RequestBody Handle handle){
        if(handle==null||handle.getNum()==null||handle.getInput()==null)
                return "您的输入不合法";
        StringBuilder sb = new StringBuilder();
        try {
            handleInput(handle.getNum(),handle.getInput());
            int len = VT.size();
            VTTABLE = new String[len+1][len+1];
            final int[] i = {1};
            VT.forEach((s)->{VTTABLE[0][i[0]]=s;
                VTTABLE[i[0]][0]=s;S2I.put(s,i[0]);i[0]++;});
            // 终结符的FIRST即本身,且终结符无FOLLOW
            VN.forEach((k)->{FOLLOW.put(k,new HashSet<>());RELATION.put(k,new HashSet<>());FIRSTVT.put(k,new HashSet<>());});
            FOLLOW.get(begin).add("~");
            VT.forEach((s)->{Set<String> temp = new HashSet<>();temp.add(s);FIRST.put(s,temp);});
            if(flag1)
                VN.forEach((s)->{FIRSTVT.put(s,handleFirstVT(s));LASTVT.put(s,handleLastVT(s));});
            if(flag){
                VN.forEach((s)->{FIRST.put(s,handleFirst(s));});
                VN.forEach((s)->{
                    handleFollow(s);
                });
                FOLLOW.forEach((k,v)->{
                    v.remove("#");
                });
            }
            handleVtTable();
            soutSet("VT",VT,sb);
            soutSet("VN",VN,sb);
            if(flag){
                soutMap("FOLLOW",FOLLOW,sb);
                soutMap("FIRST",FIRST,sb);
            }
            if(flag1) {
                soutMap("FIRSTVT", FIRSTVT, sb);
                soutMap("LASTVT", LASTVT, sb);
                soutTable(sb);
            }
            if(!flag){
                sb.append("您的文法不符合LL(1)，无法求得FIRST、FOLLOW\n");
            }
            if(!flag1){
                sb.append("您的文法不符合算符优先文法，无法求出FIRSTVT、LASTVT\n");
            }
        }catch (Exception e){
            sb.append(e.getMessage());
        }finally {
            destroy();
        }
        return sb.toString();
    }
}
