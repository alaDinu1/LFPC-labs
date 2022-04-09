package lab3;

public class Token {
    String tokenName;
    lab3.TokenType type;



    public Token(String tokenName, lab3.TokenType type) {
        this.tokenName = tokenName;
        this.type = type;
    }
    public void printToken(){
        System.out.println(tokenName + type.toString());
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public lab3.TokenType getType() {
        return type;
    }

    public void setType(lab3.TokenType type) {
        this.type = type;
    }
}