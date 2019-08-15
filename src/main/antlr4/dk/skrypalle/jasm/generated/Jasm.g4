/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright © 2018 Peter Skrypalle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
grammar Jasm;

jasmFile
    : EOL*
      header
      (methodSpec? EOL)*
    ;

header
    : bytecodeVersion EOL+
      (source EOL)?
      classSpec EOL+
      superSpec EOL+
      (implementsSpec EOL)*
    ;

bytecodeVersion
    : '.bytecode' major=INTEGER '.' minor=INTEGER
    ;

source
    : '.source' sourceFile=string
    ;

classSpec
    : '.class' accessSpec* name=fqcn
    ;

superSpec
    : '.super' name=fqcn
    ;

implementsSpec
    : '.implements' name=fqcn
    ;

//endregion header

methodSpec
    : '.method' accessSpec* name=methodName descriptor EOL+
      instructionList?
      '.end method'
    ;

methodName
    : IDENTIFIER
    | '<init>'
    ;

descriptor
    : methodDescriptor
    | typeDescriptor
    ;

methodDescriptor
    : '(' args=argList? ')' returnType=type
    ;

typeDescriptor
    : type
    ;

argList
    : type+
    ;

instructionList
    : (instruction? EOL)+
    ;

instruction
    : 'ldc'    val=INTEGER                                           #LdcIntInstr
    | 'ldc'    val=string                                            #LdcStringInstr
    | 'istore' val=INTEGER                                           #IstoreInstr
    | 'aload'  val=INTEGER                                           #AloadInstr
    | 'iload'  val=INTEGER                                           #IloadInstr

    | 'new'   typ=fqcn                                               #NewInstr

    | 'iadd'                                                         #IaddInstr
    | 'dup'                                                          #DupInstr
    | 'pop'                                                          #PopInstr
    | 'return'                                                       #ReturnInstr

    | 'getstatic'     owner=fqcn'.'name=methodName':'desc=descriptor #GetStaticInstr
    | 'invokestatic'  owner=fqcn'.'name=methodName':'desc=descriptor #InvokeStaticInstr
    | 'invokevirtual' owner=fqcn'.'name=methodName':'desc=descriptor #InvokeVirtualInstr
    | 'invokespecial' owner=fqcn'.'name=methodName':'desc=descriptor #InvokeSpecialInstr
    ;

accessSpec
    : 'public'
    | 'private'
    | 'protected'
    | 'static'
    | 'final'
    | 'synchronized'
    | 'native'
    | 'super'
    | 'interface'
    | 'abstract'
    | 'annotation'
    | 'enum'
    | 'bridge'
    | 'volatile'
    | 'transient'
    | 'varargs'
    ;

type
    : IDENTIFIER  #PrimitiveType
    | fqcn ';'    #ClassType
    | '['type     #ArrayType
    ;

fqcn
    : IDENTIFIER
    | fqcn '/' fqcn
    ;

string
    : STRING
    ;

INIT         : '<init>'       ;
PUBLIC       : 'public'       ;
PRIVATE      : 'private'      ;
PROTECTED    : 'protected'    ;
STATIC       : 'static'       ;
FINAL        : 'final'        ;
SYNCHRONIZED : 'synchronized' ;
NATIVE       : 'native'       ;
SUPER        : 'super'        ;
INTERFACE    : 'interface'    ;
ABSTRACT     : 'abstract'     ;
ANNOTATION   : 'annotation'   ;
ENUM         : 'enum'         ;
BRIDGE       : 'bridge'       ;
VOLATILE     : 'volatile'     ;
TRANSIENT    : 'transient'    ;
VARARGS      : 'varargs'      ;

EOL         : [\r\n] +                 ;
INTEGER     : '-'?[0-9]+               ;
IDENTIFIER  : [a-zA-Z$_][a-zA-Z0-9$_]* ;
WHITESPACE  : [ \t]+        -> skip    ;
COMMENT     : '#' ~ [\r\n]* -> skip    ;
STRING      : '"' ~[\r\n]* '"'         ;
