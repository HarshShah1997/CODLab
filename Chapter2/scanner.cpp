#include <bits/stdc++.h>
using namespace std;

class Token {
    public:
        string type;
        string value;
};

Token scanner();
Token scan_digits();
bool is_identifier(char ch);

string s;
int i = 0;
ofstream myfile;

int main()
{
    myfile.open("tokens.txt");
    getline(cin, s);
    Token ans = scanner();
    while (ans.type != "$") {
        ans = scanner();
    }
    myfile.close();
    return 0;
}

Token scanner()
{
    Token ans;
    if (s[i] == ' ') {
        i++;
    }
    if (i == s.length()) {
        ans.type = "$";
    } else {
        if (s[i] >= '0' && s[i] <= '9') {
            ans = scan_digits();
        } else {
            char ch = s[i];
            i++;
            if (is_identifier(ch)) {
                ans.type = "id";
                ans.value = string (1, ch);
            } else if (ch == 'f') {
                ans.type = "floatdcl";
            } else if (ch == 'i') {
                ans.type = "intdcl";
            } else if (ch == 'p') {
                ans.type = "print";
            } else if (ch == '=') {
                ans.type = "assign";
            } else if (ch == '+') {
                ans.type = "plus";
            } else if (ch == '-') {
                ans.type = "minus";
            } else {
                cout << "LEXICAL ERROR" << endl;
            }
        }
    }
    myfile << ans.type << " " << ans.value << endl;
    return ans;
}

Token scan_digits(void)
{
    Token ans;
    ans.value = "";

    while (s[i] >= '0' && s[i] <= '9') {
        ans.value = ans.value + string(1, s[i]);
        i++;
    }
    if (s[i] != '.') {
        ans.type = "inum";
    } else {
        ans.type = "fnum";
        ans.value += ".";
        i++;
        while (s[i] >= '0' && s[i] <= '9') {
            ans.value = ans.value + string(1, s[i]);
            i++;
        }
    }
    return ans;
}

bool is_identifier(char ch) 
{
    if (ch >= 'a' && ch <= 'z' && ch != 'i' && ch != 'f' && ch != 'p') {
        return true;
    } else {
        return false;
    }
}



