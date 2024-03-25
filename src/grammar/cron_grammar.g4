grammar cron_grammar;

// Rule for the entire configuration
config: (task_definition | run_configuration)+;

// Rule for task definitions
task_definition: 'task' '{'
    'name: ' STRING
    ('files: ' option_list)?
    ('cmds: ' option_list)?
    ('output: ' STRING)?
    '}';

// Rule for file list
option_list: '[' STRING (',' STRING)* ']';

// Rule for run configuration
run_configuration
    : 'run' '{' (repeat_once | repeat_every | repeat_cron)+ '}'
    ;

repeat_once
    : 'repeat_once' '{' 'tasks:' task_list 'date:' DATE 'time:' TIME '}'
    ;

repeat_every
    : 'repeat_every' '{' 'unit:' TIME_UNIT 'value:' INT 'tasks:' task_list '}'
    ;

repeat_cron
    : 'repeat_cron' '{' 'cron:' STRING 'tasks:' task_list '}'
    ;

// Rule for task list
task_list: '[' STRING (',' STRING)* ']';

// Lexer rules
STRING: '"' ~('\'' | '\n')* '"';
DATE: INT '-' INT '-' INT;
TIME: INT ':' INT ':' INT;
TIME_UNIT: 'second' | 'minute' | 'hour' | 'day' | 'month' | 'year';
INT: [0-9]+;
WS: [ \t\r\n]+ -> skip;
LINE_COMMENT : '# ' ~'\n'* '\n' -> channel(HIDDEN) ;