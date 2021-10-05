package com.example.Entity;

/**
 * @author: 倪路
 * Time: 2021/10/5-10:09
 * StuNo: 1910400731
 * Class: 19104221
 * Description:
 */
public class Handle {
    private Integer num;
    private String input;

    public Handle() {
    }

    public Handle(Integer num, String input) {
        this.num = num;
        this.input = input;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
