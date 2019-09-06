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
      (source EOL+)?
      classSpec EOL+
      (genericSpec EOL+)?
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

genericSpec
    : '.generic' sig=genericSignature? ext=argList
    ;

superSpec
    : '.super' (name=fqcn|'null')
    ;

implementsSpec
    : '.implements' name=fqcn
    ;

memberSpec
    : methodSpec
    | fieldSpec
    ;

methodSpec
    : '.method' accessSpec* genericSignature? name=methodName descriptor EOL+
      (exceptionSpec? EOL)*
      (localVarSpec? EOL)*
      instructionList?
      '.end method'
    ;

genericSignature
    : '<' gen+ '>'
    ;

gen
    : name=IDENTIFIER ':' ext=type #TypeGen
    | name=IDENTIFIER '::' ext=type #InterfaceGen
    ;

exceptionSpec
    : '.exception' start=label end=label handler=label (typ=fqcn|'any')
    ;

localVarSpec
    : '.var' index=INTEGER name=IDENTIFIER ':' typ=typeDescriptor start=label end=label
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
    : '(' args=argList? ')' returnType=type doesThrow=throwsSpec?
    ;

throwsSpec
    : '^' typ=type
    ;

typeDescriptor
    : type
    ;

argList
    : type+
    ;

instructionList
    : ((instruction|labelDef|lookupSwitch|tableSwitch|lineDirective)? EOL)+
    ;


//// //    //  //////  //////// ////////  //     //  //////  //////// ////  ///////  //    //  //////
 //  ///   // //    //    //    //     // //     // //    //    //     //  //     // ///   // //    //
 //  ////  // //          //    //     // //     // //          //     //  //     // ////  // //
 //  // // //  //////     //    ////////  //     // //          //     //  //     // // // //  //////
 //  //  ////       //    //    //   //   //     // //          //     //  //     // //  ////       //
 //  //   /// //    //    //    //    //  //     // //    //    //     //  //     // //   /// //    //
//// //    //  //////     //    //     //  ///////   //////     //    ////  ///////  //    //  //////

instruction
    : 'ldc'    val=INTEGER                                              #LdcIntInstr
    | 'ldc'    val=DECIMAL                                              #LdcDecInstr
    | 'ldc'    val=string                                               #LdcStringInstr
    | 'ldc'    val=type                                                 #LdcTypeInstr
    | 'ldc'    val='null'                                               #LdcNullInstr
    | 'ldc'    val='NaN'                                                #LdcNaNInstr
    | 'ldc'    val='NaNf'                                               #LdcNaNfInstr
    | 'ldc'    neg='-'? val='Infinity'                                  #LdcInfinityInstr
    | 'ldc'    neg='-'? val='Infinityf'                                 #LdcInfinityfInstr

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
    | 'anewarray'   typ=fqtn                                            #AnewarrayInstr
    | 'checkcast'   typ=fqtn                                            #CheckcastInstr
    | 'instanceof'  typ=fqtn                                            #InstanceofInstr

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

    | 'getstatic'        owner=fqtn'.'name=methodName':'desc=descriptor #GetStaticInstr
    | 'putstatic'        owner=fqtn'.'name=methodName':'desc=descriptor #PutStaticInstr
    | 'getfield'         owner=fqtn'.'name=methodName':'desc=descriptor #GetFieldInstr
    | 'putfield'         owner=fqtn'.'name=methodName':'desc=descriptor #PutFieldInstr

    | 'invokevirtual'    owner=fqtn'.'name=methodName':'desc=descriptor #InvokeVirtualInstr
    | 'invokespecial'    owner=fqtn'.'name=methodName':'desc=descriptor #InvokeSpecialInstr
    | 'invokestatic'     owner=fqtn'.'name=methodName':'desc=descriptor #InvokeStaticInstr
    | 'invokeinterface'  owner=fqtn'.'name=methodName':'desc=descriptor #InvokeInterfaceInstr

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

fqtn
    : fqcn
    | arrayType
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

lineDirective
    : '.line' line=INTEGER
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
    | 'synthetic'
    | 'native'
    | 'super'
    | 'interface'
    | 'abstract'
    | 'strict'
    | 'annotation'
    | 'enum'
    | 'bridge'
    | 'volatile'
    | 'transient'
    | 'varargs'
    ;

type
    : primitiveType
    | classType
    | arrayType
    | genericType
    ;

primitiveType
    : IDENTIFIER
    ;

classType
    : fqcn ';'
    ;

genericType
    : fqcn'<'genType+'>'';'
    ;

arrayType
    : '['type
    ;

genType
    : wildcard=('+'|'-')? typeToken=type #RegularGenericType
    | '*'                          #WildcardGenericType
    ;

fqcn
    : IDENTIFIER
    | fqcn '/' fqcn
    ;

string
    : STRING
    ;


////////  ///////  //    // //////// //    //  //////
   //    //     // //   //  //       ///   // //    //
   //    //     // //  //   //       ////  // //
   //    //     // /////    //////   // // //  //////
   //    //     // //  //   //       //  ////       //
   //    //     // //   //  //       //   /// //    //
   //     ///////  //    // //////// //    //  //////


ANY                   : 'any'                    ;
NULL                  : 'null'                   ;
NAN                   : 'NaN'                    ;
NAN_F                 : 'NaNf'                   ;
INFINITY              : 'Infinity'               ;
INFINITY_F            : 'Infinityf'              ;
INIT                  : '<init>'                 ;
STATIC_INIT           : '<clinit>'               ;

   //   //                         //    //
   //   //                         //    //
   //                              //
 ////  ///   / ///   ///    ////  ////  ///   // //   ///    ////
// //   //   ///    // //  //      //    //   // //  // //  //
// //   //   //     /////  //      //    //   // //  /////   ///
// //   //   //     //     //      //    //    ///   //        //
 ////  ////  //      ///    ////    //  ////    /     ///   ////

BYTECODE_DIRECTIVE    : '.bytecode'              ;
SOURCE_DIRECTIVE      : '.source'                ;
CLASS_DIRECTIVE       : '.class'                 ;
SUPER_DIRECTIVE       : '.super'                 ;
IMPLEMENTS_DIRECTIVE  : '.implements'            ;
METHOD_DIRECTIVE      : '.method'                ;
END_METHOD_DIRECTIVE  : '.end method'            ;
FIELD_DIRECTIVE       : '.field'                 ;
EXCEPTION_DIRECTIVE   : '.exception'             ;
LINE_DIRECTIVE        : '.line'                  ;
VAR_DIRECTIVE         : '.var'                   ;
GENErIC_DIRECTIVE     : '.generic'               ;

 //                  //                         //    //
 //                  //                         //    //
                     //                         //
///   / //    ////  ////  / ///  // //   ////  ////  ///    ///   / //    ////
 //   // //  //      //   ///    // //  //      //    //   // //  // //  //
 //   // //   ///    //   //     // //  //      //    //   // //  // //   ///
 //   // //     //   //   //     // //  //      //    //   // //  // //     //
////  // //  ////     //  //      // /   ////    //  ////   ///   // //  ////

LDC_INSTR             : 'ldc'                    ;
NEWARRAY_INSTR        : 'newarray'               ;
ILOAD_INSTR           : 'iload'                  ;
LLOAD_INSTR           : 'lload'                  ;
FLOAD_INSTR           : 'fload'                  ;
DLOAD_INSTR           : 'dload'                  ;
ALOAD_INSTR           : 'aload'                  ;
ISTORE_INSTR          : 'istore'                 ;
LSTORE_INSTR          : 'lstore'                 ;
FSTORE_INSTR          : 'fstore'                 ;
DSTORE_INSTR          : 'dstore'                 ;
ASTORE_INSTR          : 'astore'                 ;
RET_INSTR             : 'ret'                    ;
NEW_INSTR             : 'new'                    ;
ANEWARRAY_INSTR       : 'anewarray'              ;
CHECKCAST_INSTR       : 'checkcast'              ;
INSTANCEOF_INSTR      : 'instanceof'             ;
MULTIANEWARRAY_INSTR  : 'multianewarray'         ;
NOP_INSTR             : 'nop'                    ;
IALOAD_INSTR          : 'iaload'                 ;
LALOAD_INSTR          : 'laload'                 ;
FALOAD_INSTR          : 'faload'                 ;
DALOAD_INSTR          : 'daload'                 ;
AALOAD_INSTR          : 'aaload'                 ;
BALOAD_INSTR          : 'baload'                 ;
CALOAD_INSTR          : 'caload'                 ;
SALOAD_INSTR          : 'saload'                 ;
IASTORE_INSTR         : 'iastore'                ;
LASTORE_INSTR         : 'lastore'                ;
FASTORE_INSTR         : 'fastore'                ;
DASTORE_INSTR         : 'dastore'                ;
AASTORE_INSTR         : 'aastore'                ;
BASTORE_INSTR         : 'bastore'                ;
CASTORE_INSTR         : 'castore'                ;
SASTORE_INSTR         : 'sastore'                ;
POP_INSTR             : 'pop'                    ;
POP2_INSTR            : 'pop2'                   ;
DUP_INSTR             : 'dup'                    ;
DUP_X1_INSTR          : 'dup_x1'                 ;
DUP_X2_INSTR          : 'dup_x2'                 ;
DUP2_INSTR            : 'dup2'                   ;
DUP2_X1_INSTR         : 'dup2_x1'                ;
DUP2_X2_INSTR         : 'dup2_x2'                ;
SWAP_INSTR            : 'swap'                   ;
IADD_INSTR            : 'iadd'                   ;
LADD_INSTR            : 'ladd'                   ;
FADD_INSTR            : 'fadd'                   ;
DADD_INSTR            : 'dadd'                   ;
ISUB_INSTR            : 'isub'                   ;
LSUB_INSTR            : 'lsub'                   ;
FSUB_INSTR            : 'fsub'                   ;
DSUB_INSTR            : 'dsub'                   ;
IMUL_INSTR            : 'imul'                   ;
LMUL_INSTR            : 'lmul'                   ;
FMUL_INSTR            : 'fmul'                   ;
DMUL_INSTR            : 'dmul'                   ;
IDIV_INSTR            : 'idiv'                   ;
LDIV_INSTR            : 'ldiv'                   ;
FDIV_INSTR            : 'fdiv'                   ;
DDIV_INSTR            : 'ddiv'                   ;
IREM_INSTR            : 'irem'                   ;
LREM_INSTR            : 'lrem'                   ;
FREM_INSTR            : 'frem'                   ;
DREM_INSTR            : 'drem'                   ;
INEG_INSTR            : 'ineg'                   ;
LNEG_INSTR            : 'lneg'                   ;
FNEG_INSTR            : 'fneg'                   ;
DNEG_INSTR            : 'dneg'                   ;
ISHL_INSTR            : 'ishl'                   ;
LSHL_INSTR            : 'lshl'                   ;
ISHR_INSTR            : 'ishr'                   ;
LSHR_INSTR            : 'lshr'                   ;
IUSHR_INSTR           : 'iushr'                  ;
LUSHR_INSTR           : 'lushr'                  ;
IAND_INSTR            : 'iand'                   ;
LAND_INSTR            : 'land'                   ;
IOR_INSTR             : 'ior'                    ;
LOR_INSTR             : 'lor'                    ;
IXOR_INSTR            : 'ixor'                   ;
LXOR_INSTR            : 'lxor'                   ;
I2L_INSTR             : 'i2l'                    ;
I2F_INSTR             : 'i2f'                    ;
I2D_INSTR             : 'i2d'                    ;
L2I_INSTR             : 'l2i'                    ;
L2F_INSTR             : 'l2f'                    ;
L2D_INSTR             : 'l2d'                    ;
F2I_INSTR             : 'f2i'                    ;
F2L_INSTR             : 'f2l'                    ;
F2D_INSTR             : 'f2d'                    ;
D2I_INSTR             : 'd2i'                    ;
D2L_INSTR             : 'd2l'                    ;
D2F_INSTR             : 'd2f'                    ;
I2B_INSTR             : 'i2b'                    ;
I2C_INSTR             : 'i2c'                    ;
I2S_INSTR             : 'i2s'                    ;
LCMP_INSTR            : 'lcmp'                   ;
FCMPL_INSTR           : 'fcmpl'                  ;
FCMPG_INSTR           : 'fcmpg'                  ;
DCMPL_INSTR           : 'dcmpl'                  ;
DCMPG_INSTR           : 'dcmpg'                  ;
IRETURN_INSTR         : 'ireturn'                ;
LRETURN_INSTR         : 'lreturn'                ;
FRETURN_INSTR         : 'freturn'                ;
DRETURN_INSTR         : 'dreturn'                ;
ARETURN_INSTR         : 'areturn'                ;
RETURN_INSTR          : 'return'                 ;
ARRAYLENGTH_INSTR     : 'arraylength'            ;
ATHROW_INSTR          : 'athrow'                 ;
MONITORENTER_INSTR    : 'monitorenter'           ;
MONITOREXIT_INSTR     : 'monitorexit'            ;
GETSTATIC_INSTR       : 'getstatic'              ;
PUTSTATIC_INSTR       : 'putstatic'              ;
GETFIELD_INSTR        : 'getfield'               ;
PUTFIELD_INSTR        : 'putfield'               ;
INVOKEVIRTUAL_INSTR   : 'invokevirtual'          ;
INVOKESPECIAL_INSTR   : 'invokespecial'          ;
INVOKESTATIC_INSTR    : 'invokestatic'           ;
INVOKEINTERFACE_INSTR : 'invokeinterface'        ;
IFEQ_INSTR            : 'ifeq'                   ;
IFNE_INSTR            : 'ifne'                   ;
IFLT_INSTR            : 'iflt'                   ;
IFGE_INSTR            : 'ifge'                   ;
IFGT_INSTR            : 'ifgt'                   ;
IFLE_INSTR            : 'ifle'                   ;
IF_ICMPEQ_INSTR       : 'if_icmpeq'              ;
IF_ICMPNE_INSTR       : 'if_icmpne'              ;
IF_ICMPLT_INSTR       : 'if_icmplt'              ;
IF_ICMPGE_INSTR       : 'if_icmpge'              ;
IF_ICMPGT_INSTR       : 'if_icmpgt'              ;
IF_ICMPLE_INSTR       : 'if_icmple'              ;
IF_ACMPEQ_INSTR       : 'if_acmpeq'              ;
IF_ACMPNE_INSTR       : 'if_acmpne'              ;
GOTO_INSTR            : 'goto'                   ;
JSR_INSTR             : 'jsr'                    ;
IFNULL_INSTR          : 'ifnull'                 ;
IFNONNULL_INSTR       : 'ifnonnull'              ;
IINC_INSTR            : 'iinc'                   ;

LOOKUPSWITCH_INSTR    : 'lookupswitch'           ;
TABLESWITCH_INSTR     : 'tableswitch'            ;
ENDSWITCH_INSTR       : 'endswitch'              ;
DEFAULT_INSTR         : 'default'                ;

                             //                  //    //
                             //                  //    //
                             //                  //
////   // //  / //    ////  ////  // //   ////  ////  ///    ///   / //
// //  // //  // //  //      //   // //  // //   //    //   // //  // //
// //  // //  // //  //      //   // //  // //   //    //   // //  // //
// //  // //  // //  //      //   // //  // //   //    //   // //  // //
////    // /  // //   ////    //   // /   // /    //  ////   ///   // //
//
//

DOT                   : '.'                      ;
L_PAREN               : '('                      ;
R_PAREN               : ')'                      ;
COLON                 : ':'                      ;
DOUBLE_COLON          : '::'                     ;
SEMICOLON             : ';'                      ;
L_BRACKET             : '['                      ;
SLASH                 : '/'                      ;
LT                    : '<'                      ;
GT                    : '>'                      ;
CARET                 : '^'                      ;
PLUS                  : '+'                      ;
MINUS                 : '-'                      ;
ASTERISK              : '*'                      ;


 ////   ////   ////   ///    ////   ////   ///   / ///   ////
// //  //     //     // //  //     //     // //  ///    //
// //  //     //     /////   ///    ///   // //  //      ///
// //  //     //     //        //     //  // //  //        //
 // /   ////   ////   ///   ////   ////    ///   //     ////

PUBLIC                : 'public'                 ;
PRIVATE               : 'private'                ;
PROTECTED             : 'protected'              ;
STATIC                : 'static'                 ;
FINAL                 : 'final'                  ;
SYNCHRONIZED          : 'synchronized'           ;
SYNTHETIC             : 'synthetic'              ;
NATIVE                : 'native'                 ;
SUPER                 : 'super'                  ;
INTERFACE             : 'interface'              ;
ABSTRACT              : 'abstract'               ;
STRICT                : 'strict'                 ;
ANNOTATION            : 'annotation'             ;
ENUM                  : 'enum'                   ;
BRIDGE                : 'bridge'                 ;
VOLATILE              : 'volatile'               ;
TRANSIENT             : 'transient'              ;
VARARGS               : 'varargs'                ;

 //          //                                        ///
 //          //                                         //
 //          //                                         //
////   ///   // //   ///   / //          / ///  // //   //    ///    ////
 //   // //  ////   // //  // //         ///    // //   //   // //  //
 //   // //  ///    /////  // //         //     // //   //   /////   ///
 //   // //  ////   //     // //         //     // //   //   //        //
  //   ///   // //   ///   // //         //      // /  ////   ///   ////

EOL                   : [\r\n] +                                 ;
INTEGER               : '-'?[0-9]+[l]?                           ;
DECIMAL               : '-'?[0-9]*'.'[0-9]+([E][-]?[0-9]+)?[f]?  ;
IDENTIFIER            : [a-zA-Z$_][a-zA-Z0-9$_]*                 ;
WHITESPACE            : [ \t]+        -> skip                    ;
COMMENT               : '#' ~[\r\n]*  -> skip                    ;
STRING                : '"' ~[\r\n]* '"'                         ;
