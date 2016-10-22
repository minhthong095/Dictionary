package aloha.dictionary;


public class Word {

    private String detail;
    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }

    private String word;
    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }

    public String toString()
    {
        return word;
    }

    public Word(String b){
        word = b;
    }

    public Word(String b,String c){
        word = b;
        detail = c;
    }


}
