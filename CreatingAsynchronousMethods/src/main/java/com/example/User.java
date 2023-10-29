package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// JSON 데이터를 Java 객체로 역직렬화할 때 사용
// ignoreUnknown 속성은 기본적으로 false로 설정되어 있습니다. 이 속성을 true로 설정하면 Jackson은 JSON 데이터와 Java 클래스 간의 필드 이름이 일치하지 않아도 예외를 발생시키지 않고 무시합니다.
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String name;
    private String blog;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    @Override
    public String toString() {
        return "User [name=" + name + ", blog=" + blog + "]";
    }

}
