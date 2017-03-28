#include <bits/stdc++.h>
using namespace std;

class Node {
    public:
        Node *left;
        Node *right;
        char value;
        int position;

        bool nullable;
        set<int> firstpos;
        set<int> lastpos;

        Node(Node *l, Node *r, char v) {
            left = l;
            right = r;
            value = v;
        }

        Node(Node *l, char v) {
            left = l;
            right = NULL;
            value = v;
        }

        Node(char v, int p) {
            left = NULL;
            right = NULL;
            value = v;
            position = p;
        }
};

Node *create_tree(string regex, map< int, set<int> > &followpos);
void process_symbol(char symbol, stack<Node*> &s, int &position, map< int, set<int> > &followpos);
void print_tree(Node *tree);
void compute_nullable(Node *tree);
void compute_firstpos(Node *tree);
void union_firstpos(Node *tree);
void print_set(const set<int> &s);
void union_lastpos(Node *tree);
void compute_lastpos(Node *tree);
void compute_followpos(Node *tree, map< int, set<int> > &followpos);
void find_for_star(Node *tree, map< int, set<int> > &followpos);
void find_for_cat(Node *tree, map< int, set<int> > &followpos);
void print_map(map<int, set<int> > &fp);
map< set<int>, vector< set<int> > > create_dfa(Node *tree, vector< set<int> > &positions, map< int, set<int> > &followpos, vector<char> &symbols);
vector< set<int> > find_positions(Node *tree, vector<char> &symbols);
void find_pos_symbol(Node *tree, char symbol, set<int> &symbol_position);
void print_vector(vector< set<int> > &v, vector<char> &symbols);
set<int> union_all(set<int> intersected, map< int, set<int> > &followpos);
void print_dfa(map< set<int>, vector< set<int> > > &dfa, vector<char> &symbols);
vector<char> find_symbols(string input);
string to_postfix(string input);
string add_conc(string input);
bool is_terminal(char symbol);

int main()
{
    string input;
    cin >> input;

    input = add_conc(input);
    cout << "Input: " << input << endl;

    input = to_postfix(input);
    input += "#.";
    cout << "Postfix: " << input << endl;

    map< int, set<int> > followpos;

    Node *tree = create_tree(input, followpos);
    compute_nullable(tree);
    compute_firstpos(tree);
    compute_lastpos(tree);
    
    compute_followpos(tree, followpos);
    
    print_tree(tree);
    cout << "Followpos" << endl;
    print_map(followpos);
    cout << endl;

    vector<char> symbols = find_symbols(input);
    vector< set<int> > positions = find_positions(tree, symbols);
    cout << "Positions" << endl;
    print_vector(positions, symbols);
    cout << endl;

    map< set<int>, vector< set<int> > > dfa = create_dfa(tree, positions, followpos, symbols);
    print_dfa(dfa, symbols);

    return 0;
}

string add_conc(string input) 
{
    string ans = "";
    ans += input[0];
    for (int i = 1; i < input.length(); i++) {
        if (is_terminal(input[i]) || input[i] == '(' || input[i] == '#') {
            if (is_terminal(input[i-1]) || input[i-1] == ')' || input[i-1] == '*') {
                ans += '.';
            }
        }
        ans += input[i];
    }
    return ans;
}

string to_postfix(string input)
{
    string ans = "";
    stack<char> s;

    for (int i = 0; i < input.length(); i++) {
        if (input[i] == '(') {
            s.push(input[i]);
        } else if (input[i] == '*') {
            while (!s.empty() && s.top() == '*') {
                ans += s.top();
                s.pop();
            }
            s.push(input[i]);
        } else if (input[i] == ')') {
            while (!s.empty() && s.top() != '(') {
                ans += s.top();
                s.pop();
            }
            s.pop();

        } else if (input[i] == '.') {
            while (!s.empty() && (s.top() == '*' || s.top() == '.')) {
                ans += s.top();
                s.pop();
            }
            s.push(input[i]);
        } else if (input[i] == '|') {
            while (!s.empty() && (s.top() == '*' || s.top() == '.' || s.top() == '|')) {
                ans += s.top();
                s.pop();
            }
            s.push(input[i]);
        } else {
            ans += input[i];
        }
    }
    if (!s.empty()) {
        ans += s.top();
        s.pop();
    }

    return ans;
}

void compute_nullable(Node *tree)
{
    if (tree == NULL) {
        return;
    }
    compute_nullable(tree -> left);
    compute_nullable(tree -> right);

    if (tree -> left == NULL && tree -> right == NULL) {
        tree -> nullable = false;
    } else if (tree -> value == '*') {
        tree -> nullable = true;
    } else if (tree -> value == '|') {
        tree -> nullable = (tree -> left -> nullable || tree -> right -> nullable);
    } else {
        tree -> nullable = (tree -> left -> nullable && tree -> right -> nullable);
    }
}

void compute_firstpos(Node *tree)
{
    if (tree == NULL) {
        return;
    }
    compute_firstpos(tree -> left);
    compute_firstpos(tree -> right);

    if (tree -> left == NULL && tree -> right == NULL) {
        (tree -> firstpos).insert(tree -> position);
    } else if (tree -> value == '*') {
        tree -> firstpos = tree -> left -> firstpos;
    } else if (tree -> value == '|') {
        union_firstpos(tree);
    } else {
        if (tree -> left -> nullable == true) {
            union_firstpos(tree);
        } else {
            tree -> firstpos = tree -> left -> firstpos;
        }
    }
}

void compute_lastpos(Node *tree)
{
    if (tree == NULL) {
        return;
    }
    compute_lastpos(tree -> left);
    compute_lastpos(tree -> right);

    if (tree -> left == NULL && tree -> right == NULL) {
        (tree -> lastpos).insert(tree -> position);
    } else if (tree -> value == '*') {
        tree -> lastpos = tree -> left -> lastpos;
    } else if (tree -> value == '|') {
        union_lastpos(tree);
    } else {
        if (tree -> right -> nullable == true) {
            union_lastpos(tree);
        } else {
            tree -> lastpos = tree -> right -> lastpos;
        }
    }
}

void compute_followpos(Node *tree, map< int, set<int> > &followpos)
{
    if (tree == NULL) {
        return;
    }
    compute_followpos(tree -> left, followpos);
    compute_followpos(tree -> right, followpos);
    if (tree -> value == '*') {
        find_for_star(tree, followpos);
    } else if (tree -> value == '.') {
        find_for_cat(tree, followpos);
    }
}

map< set<int>, vector< set<int> > > create_dfa(Node *tree, vector< set<int> > &positions, 
        map< int, set<int> > &followpos, vector<char> &symbols)
{
    map< set<int>, vector< set<int> > > dfa;
    queue< set<int> > undiscovered;

    undiscovered.push(tree -> firstpos);

    while (!undiscovered.empty()) {
        set<int> state = undiscovered.front();
        undiscovered.pop();

        if (dfa.find(state) != dfa.end()) {
            continue;
        }
        vector< set<int> > transitions;
        for (int i = 0; i < symbols.size(); i++) {
            /*Intersection of positions[i] and state*/
            set<int> intersected;
            set_intersection(positions[i].begin(), positions[i].end(),
                    state.begin(), state.end(),
                    inserter(intersected, intersected.begin()));
            set<int> current_transition = union_all(intersected, followpos);

            if (dfa.find(current_transition) == dfa.end()) {
                undiscovered.push(current_transition);
            }
            transitions.push_back(current_transition);
        }
        dfa[state] = transitions;
    }
    return dfa;
}

set<int> union_all(set<int> intersected, map< int, set<int> > &followpos)
{
    set<int> ans;
    for (set<int>::iterator it = intersected.begin();
            it != intersected.end(); it++) {
        set<int> current = followpos[*it];
        set_union(current.begin(), current.end(),
                ans.begin(), ans.end(),
                inserter(ans, ans.begin()));
    }
    return ans;
}

vector< set<int> > find_positions(Node *tree, vector<char> &symbols)
{
    vector< set<int> > positions;

    for (int i = 0; i < symbols.size(); i++) {
        set<int> symbol_position;
        find_pos_symbol(tree, symbols[i], symbol_position);
        positions.push_back(symbol_position);
    }
    return positions;
}

void find_pos_symbol(Node *tree, char symbol, set<int> &symbol_position)
{
    if (tree == NULL) {
        return;
    }
    find_pos_symbol(tree -> left, symbol, symbol_position);
    find_pos_symbol(tree -> right, symbol, symbol_position);
    if (tree -> value == symbol) {
        symbol_position.insert(tree -> position);
    }
}

vector<char> find_symbols(string input)
{
    vector<char> symbols;
    map<char, bool> visited;
    for (int i = 0; i < input.length(); i++) {
        if (is_terminal(input[i])) {
            if (visited.find(input[i]) == visited.end()) {
                symbols.push_back(input[i]);
                visited[input[i]] = true;
            }
        }
    }
    return symbols;
}

void find_for_star(Node *tree, map< int, set<int> > &followpos)
{
    for (set<int>::iterator it = (tree -> lastpos).begin(); 
            it != (tree -> lastpos).end(); it++) {
        set_union(followpos[*it].begin(), followpos[*it].end(), 
                (tree -> firstpos).begin(), (tree -> firstpos).end(), 
                inserter(followpos[*it], followpos[*it].end()));
    }
}

void find_for_cat(Node *tree, map< int, set<int> > &followpos)
{
    Node *left = tree -> left;
    Node *right = tree -> right;

    for (set<int>::iterator it = (left -> lastpos).begin(); 
            it != (left -> lastpos).end(); it++) {
        set_union(followpos[*it].begin(), followpos[*it].end(),
                (right -> firstpos).begin(), (right -> firstpos).end(),
                inserter(followpos[*it], followpos[*it].end()));
    }
}

void union_firstpos(Node *tree) 
{
    set<int> left = tree -> left -> firstpos;
    set<int> right = tree -> right -> firstpos;
    set_union(left.begin(), left.end(), right.begin(), right.end(), 
            inserter(tree -> firstpos, (tree -> firstpos).begin()));
}

void union_lastpos(Node *tree) 
{
    set<int> left = tree -> left -> lastpos;
    set<int> right = tree -> right -> lastpos;
    set_union(left.begin(), left.end(), right.begin(), right.end(), 
            inserter(tree -> lastpos, (tree -> lastpos).begin()));
}

Node *create_tree(string regex, map< int, set<int> > &followpos) 
{
    /*Regex must be in postfix*/
    stack<Node*> s;
    int position = 1;
    for (int i = 0; i < regex.length(); i++) {
        char symbol = regex[i];
        process_symbol(symbol, s, position, followpos);
    }
    Node *tree = s.top();
    s.pop();
    return tree;
}

void process_symbol(char symbol, stack<Node*> &s, int &position, map< int, set<int> > &followpos) 
{
    if (symbol == '.' || symbol == '|') {
        Node *right = s.top();
        s.pop();
        Node *left = s.top();
        s.pop();
        Node *combined = new Node(left, right, symbol);
        s.push(combined);
    } else if (symbol == '*') {
        Node *left = s.top();
        s.pop();
        Node *combined = new Node(left, symbol);
        s.push(combined);
    } else {
        s.push(new Node(symbol, position));
        followpos[position] = set<int>();
        position++;
    }
}

void print_tree(Node *tree)
{
    cout << "Tree" << endl;
    cout << "Level symbol position nullable firstpos lastpos" << endl;
    queue< pair<Node*, int> > q;
    q.push(make_pair(tree, 0));

    while (!q.empty()) {
        Node *current = q.front().first;
        int level = q.front().second;
        q.pop();

        cout << level << "\t" << current -> value  << "\t" << current -> position << "\t" << current -> nullable << "\t";
        print_set(current -> firstpos);
        cout << " ";
        print_set(current -> lastpos);
        cout << endl;

        if (current -> left != NULL) {
            q.push(make_pair(current -> left, level + 1));
        }
        if (current -> right != NULL) {
            q.push(make_pair(current -> right, level + 1));
        }
    }
    cout << endl;
}

void print_dfa(map< set<int>, vector< set<int> > > &dfa, vector<char> &symbols)
{
    cout << "DFA" << endl;
    cout << "state ";
    for (int i = 0; i < symbols.size(); i++) {
        cout << symbols[i] << " ";
    }
    cout << endl;
    for (map< set<int>, vector< set<int> > >::iterator it = dfa.begin();
            it != dfa.end(); it++) {
        print_set((*it).first);
        cout << " ";
        for (int i = 0; i < ((*it).second).size(); i++) {
            print_set(((*it).second)[i]);
            cout << " ";
        }
        cout << endl;
    }
}

void print_set(const set<int> &s)
{
    for (set<int>::iterator it = s.begin(); it != s.end(); it++) {
        cout << *it << "-";
    }
}

void print_map(map<int, set<int> > &fp)
{
    for (map<int, set<int> >::iterator it = fp.begin();
            it != fp.end(); it++) {
        cout << (*it).first << " ";
        print_set((*it).second);
        cout << endl;
    }
}

void print_vector(vector< set<int> > &v, vector<char> &symbols)
{
    for (int i = 0; i < v.size(); i++) {
        cout << symbols[i] << " ";
        print_set(v[i]);
        cout << endl;
    }
}

bool is_terminal(char symbol) {
    if ((symbol >= 'a' && symbol <= 'z') || (symbol >= 'A' && symbol <= 'Z')) {
        return true;
    } else {
        return false;
    }
}



