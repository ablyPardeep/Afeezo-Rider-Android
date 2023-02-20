package com.rider.afeezo.generic

import android.os.Environment
import com.rider.afeezo.BuildConfig

object Constant {

    const val KHARAR_ZIRAKPUR = "{\n" +
            "   \"geocoded_waypoints\" : [\n" +
            "      {\n" +
            "         \"geocoder_status\" : \"OK\",\n" +
            "         \"place_id\" : \"ChIJUYrd7VflDzkRDJpQNTzJ3pw\",\n" +
            "         \"types\" : [ \"establishment\", \"point_of_interest\" ]\n" +
            "      },\n" +
            "      {\n" +
            "         \"geocoder_status\" : \"OK\",\n" +
            "         \"place_id\" : \"ChIJyeuQ4zyVDzkRqotGRW3op5o\",\n" +
            "         \"types\" : [ \"establishment\", \"point_of_interest\" ]\n" +
            "      }\n" +
            "   ],\n" +
            "   \"routes\" : [\n" +
            "      {\n" +
            "         \"bounds\" : {\n" +
            "            \"northeast\" : {\n" +
            "               \"lat\" : 30.7416068,\n" +
            "               \"lng\" : 76.81734999999999\n" +
            "            },\n" +
            "            \"southwest\" : {\n" +
            "               \"lat\" : 30.6162926,\n" +
            "               \"lng\" : 76.63991639999999\n" +
            "            }\n" +
            "         },\n" +
            "         \"copyrights\" : \"Map data ©2022\",\n" +
            "         \"legs\" : [\n" +
            "            {\n" +
            "               \"distance\" : {\n" +
            "                  \"text\" : \"30.8 km\",\n" +
            "                  \"value\" : 30834\n" +
            "               },\n" +
            "               \"duration\" : {\n" +
            "                  \"text\" : \"45 mins\",\n" +
            "                  \"value\" : 2698\n" +
            "               },\n" +
            "               \"end_address\" : \"Scf 12, Lohgarh Rd, Zirakpur, Punjab 140603, India\",\n" +
            "               \"end_location\" : {\n" +
            "                  \"lat\" : 30.64249879999999,\n" +
            "                  \"lng\" : 76.8173443\n" +
            "               },\n" +
            "               \"start_address\" : \"Kharar, New Hari Enclave, Kharar, Punjab 140301, India\",\n" +
            "               \"start_location\" : {\n" +
            "                  \"lat\" : 30.740126,\n" +
            "                  \"lng\" : 76.63991639999999\n" +
            "               },\n" +
            "               \"steps\" : [\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"73 m\",\n" +
            "                        \"value\" : 73\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"1 min\",\n" +
            "                        \"value\" : 15\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 30.740417,\n" +
            "                        \"lng\" : 76.6405864\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Head \\u003cb\\u003enortheast\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by food market (on the right)\\u003c/div\\u003e\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"y|rzDovwrMGQQc@[u@CK?E?C?C\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 30.740126,\n" +
            "                        \"lng\" : 76.63991639999999\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"0.1 km\",\n" +
            "                        \"value\" : 117\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"1 min\",\n" +
            "                        \"value\" : 38\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 30.7395357,\n" +
            "                        \"lng\" : 76.6412131\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e toward \\u003cb\\u003eBadala Road\\u003c/b\\u003e/\\u003cwbr/\\u003e\\u003cb\\u003eKheri Chowk - Kharar Rd\\u003c/b\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-right\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"s~rzDuzwrM@A@ANM`@STMFA@?B??@VS|@s@\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 30.740417,\n" +
            "                        \"lng\" : 76.6405864\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"0.4 km\",\n" +
            "                        \"value\" : 381\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"1 min\",\n" +
            "                        \"value\" : 72\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 30.7415816,\n" +
            "                        \"lng\" : 76.6443786\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e at SARPANCH dairy onto \\u003cb\\u003eBadala Road\\u003c/b\\u003e/\\u003cwbr/\\u003e\\u003cb\\u003eKheri Chowk - Kharar Rd\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Lucky Submersible Service (on the right)\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-left\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"cyrzDq~wrMk@_AYc@IIa@s@EIGIS]Q[uAyBCC_A{AU[U_@O[EMCKI_@G]\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 30.7395357,\n" +
            "                        \"lng\" : 76.6412131\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"14.8 km\",\n" +
            "                        \"value\" : 14826\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"23 mins\",\n" +
            "                        \"value\" : 1354\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 30.6162926,\n" +
            "                        \"lng\" : 76.6936357\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e at Punjab Electricals onto \\u003cb\\u003eNH 205A\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by State Bank Of India ATM (on the left in 5.6&nbsp;km)\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-right\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"{eszDkrxrMEOxAe@t@U`A[lA[xAi@r@UFChCs@BAd@Q`@M^Mr@Sl@O`@Mx@UxBq@bAY`@M`@M\\\\Kp@OJEPExBo@lA]~C_A|@Y`Ba@hA]hCy@bCw@vDcAJEfBe@dA_@bDcAxBs@h@QjA]hD}@vAe@hBi@?AHAdBi@`@MVIhEoA\\\\MzC{@pBk@rC{@hBe@jAYj@Ob@MZM`@OxF}AzAg@JCd@Oz@W^KVIr@Uj@QpBm@b@OvBm@zDiA~@Y\\\\K~Ae@ZKtBk@xAa@b@MdKsCdFuAnLoDB?tFcB`@M|@YdAYtAa@jA]vEsANEpGeBtE{AlC}@l@UzC}@bAYZKzAa@bAWPGlA[zAa@~Bk@fBe@~@WXGfFyAv@YdA]xCy@h@O~@YbCm@dA]rA]f@OlCs@zC{@p@WTKh@YTS\\\\]FGNUTYVe@\\\\w@j@yARg@Ra@Ra@^k@X_@RSj@m@RQVQPKTOd@Ur@W`@Kl@Mr@Kr@G@Ab@Az@Iv@Cd@Cp@Ef@Ar@?|@B~@FF@bBR~C^bAHj@@D@T?P?F?@?l@A`@CNCh@G\\\\G\\\\Ip@Qv@UfAYxAc@hAYdA[JCVKvHsBlA[jBe@~CcAz@YbA]bBm@BAXIVC`IsCz@YxBu@TId@Q|@[pAe@dA]rJcDlEwAlBm@h@QjBu@bDiANE~EaBnC}@tEyA|@]PGl@SvE}AbA_@b@MpDmAj@SnAa@ZKlNyEl@UREbA_@dJqCh[sKTG~Am@@?`H_C|DsA|Bs@vAa@\\\\Kz@IbGyAx@Qn@OzA]zKkCjN}C`E}@tT{E`IkBhAW~EkA`EaAbLoC|A_@tA]lFqAv@S`@Kh@K\\\\G^Ir@KtCSfBKp@Gj@I`@IRETG`@OZK^O^Ql@Yj@Y`@QvCwA`Bo@~A{@\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 30.7415816,\n" +
            "                        \"lng\" : 76.6443786\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"6.4 km\",\n" +
            "                        \"value\" : 6419\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"7 mins\",\n" +
            "                        \"value\" : 414\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 30.6425529,\n" +
            "                        \"lng\" : 76.75312219999999\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eIT City Rd\\u003c/b\\u003e/\\u003cwbr/\\u003e\\u003cb\\u003eMohali Airport Rd\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by BSV Shuttering Store (on the right in 3.3&nbsp;km)\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-left\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"yvzyDgfbsMWaAiIm[s@sCcByGi@mBwB{H_BuGaJc[cAmDaD}LuCcL}DiNaE{OW_AeEqNuFcReDyJiCuHa@mAUm@cMg_@kBaFSe@M]kAuCq@_Bq@aBcB_EO]Qe@eC_G]u@Uk@sAyCcA_CcA_CoCsFgA}BiAeCqCgGyA_D}A_DyDwHMSKWO_@IWSm@Ma@ISc@qAg@kAe@cAm@qAy@_B]s@IMGKACCEAAEEACCAACECIGECGCEAEC\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 30.6162926,\n" +
            "                        \"lng\" : 76.6936357\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"4.2 km\",\n" +
            "                        \"value\" : 4157\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"5 mins\",\n" +
            "                        \"value\" : 294\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 30.6226948,\n" +
            "                        \"lng\" : 76.7878755\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"At \\u003cb\\u003eMohali Airport Chowk\\u003c/b\\u003e, take the \\u003cb\\u003e3rd\\u003c/b\\u003e exit onto \\u003cb\\u003eNH 5\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Soni Studio (on the left)\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\" : \"roundabout-left\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"}z_zD_zmsMC?G?MCGCCAAACAAACAA?AAAACAAAAA?AAAAAAAAAACAAACAA?CAAAC?CAA?C?CAC?A?CAC@M?K?C@A?E@C?C@A?C@C@C@C@C@C@A@A@A@?@C@ABABC@ABA@A@?@A@ABAB?@A@?BA@?@?B?@AB?@?B?B@B?B?HB@?HAJCDADAJCFCFCHEDCZYX[XWv@s@p@s@JKf@g@p@w@JMtA}A~AiBlAsAlBwBfAuAxB}CfBeCLUfP}TbCoDhD_FLSjAcBtAsBJM|A{BhA_B`@k@r@cAhBkC`B{BjAeB~@sAjA_BVc@T_@b@aAJUJYJWNa@Ja@b@wA~@sCbAgE\\\\qAf@mBBIb@iBr@wCt@iDXqAH[b@iBVeA`@gBj@oCPq@DQ@IPaAf@wCLm@Jg@j@aCDSz@gEp@}ChAmFd@oB^gBHa@DOf@{B\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 30.6425529,\n" +
            "                        \"lng\" : 76.75312219999999\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"3.6 km\",\n" +
            "                        \"value\" : 3642\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"5 mins\",\n" +
            "                        \"value\" : 275\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 30.6479962,\n" +
            "                        \"lng\" : 76.8107148\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eNH 5\\u003c/b\\u003e/\\u003cwbr/\\u003e\\u003cb\\u003eNH 7\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Mansa Devi Mandir (on the left in 450&nbsp;m)\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-left\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"y~{yDgstsMGi@EsAAe@C{@GeCOuBQSIIc@c@}@{@q@q@oAiAOMqDaD}@y@w@s@u@q@aA}@g@a@c@a@wAoAKIWU][aAy@c@_@mAeASOaA{@o@i@c@_@SS}@u@u@o@uAkAwCwBcAy@iA{@qGyE{@m@SQ_As@]UMI}@i@_@SaCqAECcCyA{BqAqBkAcAm@sAu@aEcCiF_DyCuBcAs@mAaAYUsC_CaBwAsAgAgDoCgA}@yBeBkCsBqC{BoCyBwBcBWQ\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 30.6226948,\n" +
            "                        \"lng\" : 76.7878755\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"0.2 km\",\n" +
            "                        \"value\" : 194\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"1 min\",\n" +
            "                        \"value\" : 17\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 30.6493079,\n" +
            "                        \"lng\" : 76.81204079999999\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Continue straight to stay on \\u003cb\\u003eNH 5\\u003c/b\\u003e/\\u003cwbr/\\u003e\\u003cb\\u003eNH 7\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Sara infrasolutions (on the left)\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\" : \"straight\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"_}`zD}aysMkAcAe@a@a@_@QQUY]a@UYU[\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 30.6479962,\n" +
            "                        \"lng\" : 76.8107148\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"0.6 km\",\n" +
            "                        \"value\" : 552\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"2 mins\",\n" +
            "                        \"value\" : 105\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 30.6451145,\n" +
            "                        \"lng\" : 76.8150952\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e at Zirakpur Properties onto \\u003cb\\u003eLohgarh Rd\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Bir Real Estates, Zirakpur, Punjab (on the left)\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-right\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"eeazDgjysMRQFIpAy@dBkAbDcCDEfAq@lAy@b@YjAw@hBkAdAk@RA\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 30.6493079,\n" +
            "                        \"lng\" : 76.81204079999999\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"0.2 km\",\n" +
            "                        \"value\" : 150\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"1 min\",\n" +
            "                        \"value\" : 37\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 30.6440435,\n" +
            "                        \"lng\" : 76.8146933\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"At \\u003cb\\u003eSigma City Chowk\\u003c/b\\u003e, take the \\u003cb\\u003e2nd\\u003c/b\\u003e exit\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Its venture of lsys education (on the right)\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\" : \"roundabout-left\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"}j`zDk}ysMAC?E?E@CBCBCD?B?@?B@BB@B@D?D?DA@z@R~@Pz@h@\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 30.6451145,\n" +
            "                        \"lng\" : 76.8150952\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"0.3 km\",\n" +
            "                        \"value\" : 306\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"1 min\",\n" +
            "                        \"value\" : 73\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 30.6426551,\n" +
            "                        \"lng\" : 76.81734999999999\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e at Baba Tent House\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Nirmal Glass Works (on the left)\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-left\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"gd`zDyzysM^i@|@mAf@u@f@q@j@aAj@kB?CJkAB]AW\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 30.6440435,\n" +
            "                        \"lng\" : 76.8146933\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"17 m\",\n" +
            "                        \"value\" : 17\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"1 min\",\n" +
            "                        \"value\" : 4\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 30.64249879999999,\n" +
            "                        \"lng\" : 76.8173443\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e at HEALTHKART WHEEL\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eDrive along Sanatan Dharma Temple (on the left)\\u003c/div\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eDestination will be on the right\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-right\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"s{_zDmkzsM^@\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 30.6426551,\n" +
            "                        \"lng\" : 76.81734999999999\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"DRIVING\"\n" +
            "                  }\n" +
            "               ],\n" +
            "               \"traffic_speed_entry\" : [],\n" +
            "               \"via_waypoint\" : []\n" +
            "            }\n" +
            "         ],\n" +
            "         \"overview_polyline\" : {\n" +
            "            \"points\" : \"y|rzDovwrMy@wB@OPOv@a@HAB@tAgAeAcBy@qA_CwDuAwBe@{@[wAEOxAe@vBq@lA[xAi@z@YlCu@fA_@bD_AxGoBvNcE~C{@rEwAbCw@vDcArBk@lKiDtF{A`EoApC{@zKcDdGgBtD_AnA]|@]tIeCp@SfDcArTsGnGgBjRiFrLoDzKeDhKyCpGeBtE{AzDsAvIeC`QoE~NkErHuBdL}CfAc@~@m@d@e@d@o@t@}A~@aCf@cAx@kA~@aAj@c@f@[xAm@nAYfBSxCQvAIzAA|BJjBTbFh@p@Bp@?nAEx@KdDy@pHsBb@OdKoCjBe@~CcA~Bw@fBo@XIVC|JmDnC_AzFqB`Q{FvC_AjBu@bDiAnFgBtL}DvToHzOoFvAe@dJqCh[sKtBu@~QgGtBm@z@IbGyAhBa@vNiDlT{EtT{E`IkBhHcBdRqExMcDhCi@r@KtCSxCSlASh@M|@[~@a@rG}C`Bo@~A{@aJo]wCmLaDiL_BuGaJc[cAmDaD}LuCcL}DiNyE{Q{Lua@oHoTw@{BcMg_@kBaFa@cA}BuFwDeJmG{NgC_GwEqJ{EmKwD_IgEkI[w@u@{BkA}CsAuCiBmD[_@]Q_@G[OOOMWE[Fo@L[TQRIVCTD\\\\G`@MNIt@u@nCkCzDkElD}DlBwBfAuA`FcHtPsUfKgOhG}I`HwJnF}Hx@aBVo@jAsD~@sCbAgEdA_Ef@sBhBaI~A}GdB{HhAqGxBeK`EcRNq@f@{BGi@GyBKaEOuBQSm@m@oEeE}IaIiJgImJeIkC{B{EqDkLuI}AiAkAs@kHcEeKaGkLcH}EiD}IoH}L{JePmMcBuAgAaA{AgBU[RQxAcAdBkAbDcClAw@fHwEdAk@RAAI@IFGN@DF@JAFzBd@z@h@^i@dBcCrAsBj@oBNiBAW^@\"\n" +
            "         },\n" +
            "         \"summary\" : \"NH 205A\",\n" +
            "         \"warnings\" : [],\n" +
            "         \"waypoint_order\" : []\n" +
            "      }\n" +
            "   ],\n" +
            "   \"status\" : \"OK\"\n" +
            "}"

    const val LANGUAGE_CODE = "en"
    const val CASH: String="CASH"
    const val WALLET: String="WALLET"
    const val CARD: String="CARD"
    const val TIMER: Long = 60000
    const val RIDE_DETAIL_INTENT = "RIDEDETAILS"
    const val fireBaseTableName = "users"
    const val BASE_GOOGLE_APIS_URL = "https://maps.googleapis.com/"
    const val height = 120 // Height of the nearby marker icons
    const val width = 90 // Width of the nearby marker icon
    const val SEND_OTP = 1
    const val LOGIN_WITH_OTP = 2
    const val GET_NEARBY_DRIVERS = 3
    const val GET_VEHICLES = 4
    const val ADD_RIDE_REQUEST = 5
    const val CANCEL_RIDE_REQUEST = 6
    const val RIDE_DETAIL = 7
    const val RIDE_CANCEL_REASON = 8
    const val SAVE_FIREBASE_TOKEN = 9
    const val UPDATE_PROFILE = 10
    const val SET_UP_WALLET = 12
    const val GET_TEMP_TOKEN = 13
    const val USER_PROFILE_INFO = 14
    const val MY_RIDES = 15
    const val GET_ACTIVE_RIDE = 16
    const val CANCEL_RIDE = 17
    const val GET_USER_COUPONS = 18
    const val GET_OPERATING_LOC = 19
    const val GET_VEHICLE_TYPES_BY_LOC_ID = 20
    const val GET_VEHICLE_TYPES_BY_VEHICLE_ID = 21
    const val SUPPORT_CATEGORIES = 22
    const val SUPPORT_CATEGORY_DETAIL = 23
    const val TRANSACTIONS = 24
    const val WITHDRAWL_REQUEST = 25
    const val CONTACT_US_API = 26
    const val TERMS_COND_API = 27
    const val ABOUT_US_API = 28
    const val RIDE_RATE = 29
    const val COUPON_VALIDATE_API = 30
    const val ADD_CONTACT = 31
    const val GET_CONTACT = 32
    const val DELETE_CONTACT = 33
    const val GET_FAV_LOCATION = 34
    const val ADD_FAV_LOCATION = 35
    const val DELETE_FAV_LOCATION = 36
    const val END_RIDE = 37
    const val LOGOUT = 38
    const val GET_MONEY_TABS = 39
    const val UPDATE_PROFILE_PIC = 41
    const val REMOVE_PROFILE_PIC = 42
    const val RIDE_STATUSES = 43
    const val RIDE_ISSUES = 44
    const val REPORT_RIDE_ISSUE = 45
    const val SHARE_RIDE_DETAIL = 46
    const val TRIGGER_CONTACTS = 47
    const val PRIVACY_POLICY_API = 48
    const val VERIFY_REFERRAL = 49
    const val REWARD_POINTS = 50
    const val PAYMENT_OPTIONS = 51
    const val ADD_CARD = 52
    const val GET_CARDS = 53
    const val DELETE_CARD = 54
    const val RECENT_RIDE_DETAIL = 55
    const val REFER_TEXT = 56
    const val REQUEST_DATA = 57
    const val DELETE_ACCOUNT = 58
    const val GET_CONFIGURATION = 59

    const val CallTime = 20 // In Seconds
    const val oneTimeCall = "oneTimeCall" // Used to display map : true map will load : false map will not load
    const val FIREBASE_LATITUDE = "lattitude"
    const val FIREBASE_LONGITUDE = "longitude"
    const val askedPermission = "askedPermission"
    const val SESSION_EXPIRED = "-3"
    const val AUTHORIZE = "IS_LOGIN_ALREADY"
    const val RIDER_TOKEN = "RIDER_TOKEN"
    const val RIDER_NUmber = "RIDER_NUmber"
    const val RIDER_ID = "RIDER_ID"
    const val RIDE_ID = "RIDE_ID"
    const val BOOKING_ID = "BOOKING_ID"
    const val CANCEL_CHARGE = "CancelCharge"
    const val CANCEL_TEXT = "CancelText"
    const val COMMON_LOGIN = "COMMON_LOGIN"
    const val GPS = "GPS_PERMISION"

    const val WALLET_ENABLE = "WALLET_ENABLE"
    const val REFERRAL_ENABLE = "REFERRAL_ENABLE"
    const val SCHEDULE_RIDE_ENABLE = "SCHEDULE_RIDE_ENABLE"
    const val REWARD_ENABLE = "REWARD_ENABLE"

    //NotificationTypes
    const val RIDE_REQUEST_COMPLETED = "RIDE_REQUEST_COMPLETED"
    const val RIDE_COMPLETED = "RIDE_COMPLETED_RIDER"
    const val RIDE_READY_FOR_PICKUP = "RIDE_READY_FOR_PICKUP"
    const val TXN = "TXN"
    const val NOTIFICATION_TYPE = "Yocabs"
    const val NOTIFICATION_REFERRAL_REWARD_POINTS = "REFERRAL_REWARD_POINTS"
    const val RIDE_STARTED = "RIDE_STARTED"
    const val RIDE_CANCELLED = "RIDE_CANCELLED"
    const val RIDE_REQUEST_DECLINED_ALL_DRIVERS = "RIDE_REQUEST_DECLINED_ALL_DRIVERS"
    const val ASK_PROFILE_INFO = "ASK_PROFILE_INFO"
    const val ORDER_ID = "ORDER_ID"
    const val TEMP_TOKEN = "TEMP_TOKEN"
    const val URL_SEND_TO_WEB = "user-app-api/send_to_web"
    const val ONE_TIME_CALL = "ONE_TIME_CALL"
    const val USER_IMAGE_NAME = "user_profile.jpg"
    const val IS_RECENT_RIDE = "is_recent_ride"
    val IMAGE_PATH = (Environment.getExternalStorageDirectory()
        .toString() + "/Android/data/"
            + BuildConfig.APPLICATION_ID
            + "/Files/Images")
    const val HOME = 0
    const val CONTACT_US = 11
    const val ABOUT_US = 12
    const val TERMS_COND = 14
    const val PRIVACY_POLICY = 15
    const val TRANSACTION_REQUEST_CODE = 1000
    const val SEARCH_SOURCE = "SEARCH_SOURCE"
    const val SEARCH_DESTINATION = "SEARCH_DESTINATION"
    const val SEARCH_EMPTY = "SEARCH_EMPTY"
    const val FAV_SOURCE = "FAV_SOURCE"
    const val FAV_DESTINATION = "FAV_DESTINATION"
    const val COMPLETE_RIDE = "COMPLETE_RIDE"
    const val WALLET_STATUS = "WALLET_STATUS"
    const val COUNRTY_CODE = "COUNRTY_CODE"
    const val PHONE_CODE = "PHONE_CODE"
    const val USER_PHONE = "USER_PHONE"
    const val PROFILE_DATA = "PROFILE_DATA"
    const val COUPON_CODE_VALUE = "COUPON_CODE_VALUE"
    const val COUPON_MSG = "COUPON_MSG"
    const val PROFILE_DOWNLOAD = "PROFILE_DOWNLOAD"
    const val PAYMENT_METHOD = "PAYMENT_METHOD"
    const val PAYMENT_CARD = "PAYMENT_CARD"
    const val DEMO_RIDER = "1234567890"

    var CURRENCY = ""
    var currentOpen = 0
    var MIN_CANCEL_WORDS = 10 // 3 sec
    var UPDATE_INTERVAL = 3000 // 3 sec
    var FATEST_INTERVAL = 5000 // 5 sec
    var DISPLACEMENT = 10 // 10 meters
    var CANCEL_RIDE_CODE = 100
    var SAVED_PLACE_REQUEST = 200
    var RECENT_RIDE_CODE = 600
    var SAVED_CODE = "SAVED_CODE"
    var COUPON_CODE = 300
    var CONTACT_CODE = 400
    var FAQ_ID = "FAQ_ID"
    var FAQ_CATEGORY = "FAQ_CATEGORY"
    var ACTIVITY = "ACTIVITY"
    var FAV_LOCATION = "FAV_LOCATION"
    var AUTO = "AUTO"
    var BIKE = "BIKE"
    var TAXI = "TAXI"
    const val HOME_TEXT = "home"
    const val WORK_TEXT = "work"

}