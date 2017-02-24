#include <bits/stdc++.h>
using namespace std;

class Token {
    public:
        string type;
        string value;
};

Token get_token(string line);
void prog();
void dcls();
void dcl();
void stmts();
void stmt();
void expr();
void val();
void expect(string token_type);
void error(string msg);

vector<Token> token_stream;
int pos = 0;

int main()
{
    ifstream infile("tokens.txt");
    string line;
    while (getline(infile, line)) {
        Token t = get_token(line);
        cout << t.type << " " << t.value << endl;
        token_stream.push_back(t);
    }
    prog();
    return 0;
}

void prog()
{
    //Current type of token in token stream
    string curr = token_stream[pos].type;
    if (curr == "floatdcl" || curr == "intdcl" || curr == "id" || curr == "print" || curr == "$") {
        dcls();
        stmts();
        expect("$");
    } else {
        error("Expected floatdcl, intdcl, id, print, $");
    }
}

void dcls()
{
    string curr = token_stream[pos].type;
    if (curr == "floatdcl" || curr == "intdcl") {
        dcl();
        dcls();
    } else if (curr == "id" || curr == "print" || curr == "$") {
        ;
    } else {
        error("expected intdcl, floatdcl");
    }
}

void dcl()
{
    string curr = token_stream[pos].type;
    if (curr == "floatdcl") {
        expect("floatdcl");
        expect("id");
    } else if (curr == "intdcl") {
        expect("intdcl");
        expect("id");
    } else {
        error("expected intdcl, floatdcl");
    }
}

void stmts()
{
    string curr = token_stream[pos].type;
    if (curr == "id" || curr == "print") {
        stmt();
        stmts();
    } else if (curr == "$") {
        ;
    } else {
        error("expected id, print");
    }
}

void stmt()
{
    string curr = token_stream[pos].type;
    if (curr == "id") {
        expect("id");
        expect("assign");
        val();
        expr();
    } else if (curr == "print") {
        expect("print");
        expect("id");
    } else {
        error("expected id, print");
    }
}

void expr()
{
    string curr = token_stream[pos].type;
    if (curr == "plus" || curr == "minus") {
        expect(curr);
        val();
        expr();
    } else if (curr == "id" || curr == "print" || curr == "$") {
        ;
    } else {
        error("expected plus, minus");
    }
}

void val()
{
    string curr = token_stream[pos].type;
    if (curr == "id" || curr == "inum" || curr == "fnum") {
        expect(curr);
    } else {
        error("expected id, inum, fnum");
    }
}

void error(string msg)
{
    cout << "ERROR:" << msg << endl;
}

void expect(string token_type)
{
    string curr = token_stream[pos].type;
    if (curr != token_type) {
        error("expected " + token_type);
    } else {
        pos++;
    }
}

Token get_token(string line)
{
    Token t;
    bool found_space = false;
    for (int i = 0; i < line.length(); i++) {
        if (line[i] == ' ') {
            found_space = true;
        } else if (found_space) {
            t.value += line[i];
        } else {
            t.type += line[i];
        }
    }
    return t;
}

