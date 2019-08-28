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
      (memberSpec? EOL)*
    ;

header
    : bytecodeVersion EOL+
      (source EOL)?
      classSpec EOL+
      superSpec EOL+
      (implementsSpec EOL)*
    ;

bytecodeVersion
    : '.bytecode' ver=DECIMAL
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

memberSpec
    : methodSpec
    | fieldSpec
    ;

methodSpec
    : '.method' accessSpec* name=methodName descriptor EOL+
      instructionList?
      '.end method'
    ;

methodName
    : IDENTIFIER
    | '<init>'
    | '<clinit>'
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
    : ((instruction|labelDef|lookupSwitch|tableSwitch)? EOL)+
    ;

instruction
    : 'ldc'    val=INTEGER                                              #LdcIntInstr
    | 'ldc'    val=DECIMAL                                              #LdcDecInstr
    | 'ldc'    val=string                                               #LdcStringInstr
    | 'ldc'    val=type                                                 #LdcTypeInstr

    | 'newarray' typ=IDENTIFIER                                         #NewarrayInstr

    | 'iload'  val=INTEGER                                              #IloadInstr
    | 'lload'  val=INTEGER                                              #LloadInstr
    | 'fload'  val=INTEGER                                              #FloadInstr
    | 'dload'  val=INTEGER                                              #DloadInstr
    | 'aload'  val=INTEGER                                              #AloadInstr
    | 'istore' val=INTEGER                                              #IstoreInstr
    | 'lstore' val=INTEGER                                              #LstoreInstr
    | 'fstore' val=INTEGER                                              #FstoreInstr
    | 'dstore' val=INTEGER                                              #DstoreInstr
    | 'astore' val=INTEGER                                              #AstoreInstr
    | 'ret'    val=INTEGER                                              #RetInstr

    | 'new'         typ=fqcn                                            #NewInstr
    | 'anewarray'   typ=fqcn                                            #AnewarrayInstr
    | 'checkcast'   typ=fqcn                                            #CheckcastInstr
    | 'instanceof'  typ=fqcn                                            #InstanceofInstr

    | 'multianewarray' typ=typeDescriptor dim=INTEGER                   #MultianewarrayInstr

    | 'nop'                                                             #NopInstr
    | 'iaload'                                                          #IaloadInstr
    | 'laload'                                                          #LaloadInstr
    | 'faload'                                                          #FaloadInstr
    | 'daload'                                                          #DaloadInstr
    | 'aaload'                                                          #AaloadInstr
    | 'baload'                                                          #BaloadInstr
    | 'caload'                                                          #CaloadInstr
    | 'saload'                                                          #SaloadInstr
    | 'iastore'                                                         #IastoreInstr
    | 'lastore'                                                         #LastoreInstr
    | 'fastore'                                                         #FastoreInstr
    | 'dastore'                                                         #DastoreInstr
    | 'aastore'                                                         #AastoreInstr
    | 'bastore'                                                         #BastoreInstr
    | 'castore'                                                         #CastoreInstr
    | 'sastore'                                                         #SastoreInstr
    | 'pop'                                                             #PopInstr
    | 'pop2'                                                            #Pop2Instr
    | 'dup'                                                             #DupInstr
    | 'dup_x1'                                                          #DupX1Instr
    | 'dup_x2'                                                          #DupX2Instr
    | 'dup2'                                                            #Dup2Instr
    | 'dup2_x1'                                                         #Dup2X1Instr
    | 'dup2_x2'                                                         #Dup2X2Instr
    | 'swap'                                                            #SwapInstr
    | 'iadd'                                                            #IaddInstr
    | 'ladd'                                                            #LaddInstr
    | 'fadd'                                                            #FaddInstr
    | 'dadd'                                                            #DaddInstr
    | 'isub'                                                            #IsubInstr
    | 'lsub'                                                            #LsubInstr
    | 'fsub'                                                            #FsubInstr
    | 'dsub'                                                            #DsubInstr
    | 'imul'                                                            #ImulInstr
    | 'lmul'                                                            #LmulInstr
    | 'fmul'                                                            #FmulInstr
    | 'dmul'                                                            #DmulInstr
    | 'idiv'                                                            #IdivInstr
    | 'ldiv'                                                            #LdivInstr
    | 'fdiv'                                                            #FdivInstr
    | 'ddiv'                                                            #DdivInstr
    | 'irem'                                                            #IremInstr
    | 'lrem'                                                            #LremInstr
    | 'frem'                                                            #FremInstr
    | 'drem'                                                            #DremInstr
    | 'ineg'                                                            #InegInstr
    | 'lneg'                                                            #LnegInstr
    | 'fneg'                                                            #FnegInstr
    | 'dneg'                                                            #DnegInstr
    | 'ishl'                                                            #IshlInstr
    | 'lshl'                                                            #LshlInstr
    | 'ishr'                                                            #IshrInstr
    | 'lshr'                                                            #LshrInstr
    | 'iushr'                                                           #IushrInstr
    | 'lushr'                                                           #LushrInstr
    | 'iand'                                                            #IandInstr
    | 'land'                                                            #LandInstr
    | 'ior'                                                             #IorInstr
    | 'lor'                                                             #LorInstr
    | 'ixor'                                                            #IxorInstr
    | 'lxor'                                                            #LxorInstr
    | 'i2l'                                                             #I2lInstr
    | 'i2f'                                                             #I2fInstr
    | 'i2d'                                                             #I2dInstr
    | 'l2i'                                                             #L2iInstr
    | 'l2f'                                                             #L2fInstr
    | 'l2d'                                                             #L2dInstr
    | 'f2i'                                                             #F2iInstr
    | 'f2l'                                                             #F2lInstr
    | 'f2d'                                                             #F2dInstr
    | 'd2i'                                                             #D2iInstr
    | 'd2l'                                                             #D2lInstr
    | 'd2f'                                                             #D2fInstr
    | 'i2b'                                                             #I2bInstr
    | 'i2c'                                                             #I2cInstr
    | 'i2s'                                                             #I2sInstr
    | 'lcmp'                                                            #LcmpInstr
    | 'fcmpl'                                                           #FcmplInstr
    | 'fcmpg'                                                           #FcmpgInstr
    | 'dcmpl'                                                           #DcmplInstr
    | 'dcmpg'                                                           #DcmpgInstr
    | 'ireturn'                                                         #IreturnInstr
    | 'lreturn'                                                         #LreturnInstr
    | 'freturn'                                                         #FreturnInstr
    | 'dreturn'                                                         #DreturnInstr
    | 'areturn'                                                         #AreturnInstr
    | 'return'                                                          #ReturnInstr
    | 'arraylength'                                                     #ArrayLengthInstr
    | 'athrow'                                                          #AthrowInstr
    | 'monitorenter'                                                    #MonitorenterInstr
    | 'monitorexit'                                                     #MonitorexitInstr

    | 'getstatic'        owner=fqcn'.'name=methodName':'desc=descriptor #GetStaticInstr
    | 'putstatic'        owner=fqcn'.'name=methodName':'desc=descriptor #PutStaticInstr
    | 'getfield'         owner=fqcn'.'name=methodName':'desc=descriptor #GetFieldInstr
    | 'putfield'         owner=fqcn'.'name=methodName':'desc=descriptor #PutFieldInstr

    | 'invokevirtual'    owner=fqcn'.'name=methodName':'desc=descriptor #InvokeVirtualInstr
    | 'invokespecial'    owner=fqcn'.'name=methodName':'desc=descriptor #InvokeSpecialInstr
    | 'invokestatic'     owner=fqcn'.'name=methodName':'desc=descriptor #InvokeStaticInstr
    | 'invokeinterface'  owner=fqcn'.'name=methodName':'desc=descriptor #InvokeInterfaceInstr

    | 'ifeq'             dst=label                                      #IfeqInstr
    | 'ifne'             dst=label                                      #IfneInstr
    | 'iflt'             dst=label                                      #IfltInstr
    | 'ifge'             dst=label                                      #IfgeInstr
    | 'ifgt'             dst=label                                      #IfgtInstr
    | 'ifle'             dst=label                                      #IfleInstr
    | 'if_icmpeq'        dst=label                                      #IfIcmpeqInstr
    | 'if_icmpne'        dst=label                                      #IfIcmpneInstr
    | 'if_icmplt'        dst=label                                      #IfIcmpltInstr
    | 'if_icmpge'        dst=label                                      #IfIcmpgeInstr
    | 'if_icmpgt'        dst=label                                      #IfIcmpgtInstr
    | 'if_icmple'        dst=label                                      #IfIcmpleInstr
    | 'if_acmpeq'        dst=label                                      #IfAcmpeqInstr
    | 'if_acmpne'        dst=label                                      #IfAcmpneInstr
    | 'goto'             dst=label                                      #GotoInstr
    | 'jsr'              dst=label                                      #JsrInstr
    | 'ifnull'           dst=label                                      #IfnullInstr
    | 'ifnonnull'        dst=label                                      #IfnonnullInstr

    | 'iinc'             var=INTEGER inc=INTEGER                        #IincInstr
    ;

label
    : name=IDENTIFIER
    ;

labelDef
    : label ':'
    ;

lookupSwitch
    : 'lookupswitch' EOL+
      (lookupTarget? EOL)*
      defaultTarget EOL+
      'endswitch'
    ;

lookupTarget
    : val=INTEGER ':' dst=label
    ;

defaultTarget
    : 'default' ':' dst=label
    ;

tableSwitch
    : 'tableswitch' EOL+
      (lookupTarget? EOL)*
      defaultTarget EOL+
      'endswitch'
    ;

fieldSpec
    : '.field' accessSpec* name=IDENTIFIER typeDescriptor
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
STATIC_INIT  : '<clinit>'     ;
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
INTEGER     : '-'?[0-9]+[l]?           ;
DECIMAL     : '-'?[0-9]*'.'[0-9]+[f]?  ;
IDENTIFIER  : [a-zA-Z$_][a-zA-Z0-9$_]* ;
WHITESPACE  : [ \t]+        -> skip    ;
COMMENT     : '#' ~ [\r\n]* -> skip    ;
STRING      : '"' ~[\r\n]* '"'         ;
