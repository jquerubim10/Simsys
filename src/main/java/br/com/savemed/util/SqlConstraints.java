package br.com.savemed.util;

public class SqlConstraints {

    public static final String PREVIEW_EVOLUTION = """
                SELECT \
                A.EVOLUCAO, A.REGISTRO, A.DATA, A.HORA, ISNULL(B.USUARIO, 0) AS USUARIO, \s
                ISNULL(B.NOME, '') AS NOME_USUARIO, A.TUTOR, A.CENTROCUSTO,\s
                CC.DESCRICAO AS DESCRICAO_CENTROCUSTO, A.LEITO,\s
                ISNULL(UTA.DESCRICAO, '') AS NOME_TUTOR,\
                CAST(A.TEXTOFORMATADO AS VARCHAR(8000))  AS TEXTO,\s
                CASE\s
                    WHEN ISNULL(C.MEDICO, 0) <> 0 THEN\s
                        CASE\s
                            WHEN ISNULL(C.CONSELHOPROFISSIONAL, 0) = 1 THEN 'CRAS'\s
                            WHEN ISNULL(C.CONSELHOPROFISSIONAL, 0) = 2 THEN 'COREN'\s
                            WHEN ISNULL(C.CONSELHOPROFISSIONAL, 0) = 3 THEN 'CRF'\s
                            WHEN ISNULL(C.CONSELHOPROFISSIONAL, 0) = 4 THEN 'CRFA'\s
                            WHEN ISNULL(C.CONSELHOPROFISSIONAL, 0) = 5 THEN 'CREFITO'\s
                            WHEN ISNULL(C.CONSELHOPROFISSIONAL, 0) = 6 THEN 'CRM'\s
                            WHEN ISNULL(C.CONSELHOPROFISSIONAL, 0) = 7 THEN 'CRV'\s
                            WHEN ISNULL(C.CONSELHOPROFISSIONAL, 0) = 8 THEN 'CRN'\s
                            WHEN ISNULL(C.CONSELHOPROFISSIONAL, 0) = 9 THEN 'CRO'\s
                            WHEN ISNULL(C.CONSELHOPROFISSIONAL, 0) = 10 THEN 'CRP'\s
                            WHEN ISNULL(C.CONSELHOPROFISSIONAL, 0) = 11 THEN 'OUT'\s
                            WHEN ISNULL(C.CONSELHOPROFISSIONAL, 0) = 12 THEN 'CRBM'\s
                            WHEN ISNULL(C.CONSELHOPROFISSIONAL, 0) = 13 THEN 'CRESS'\s
                        END + ':' + C.CRM + '-' + SIGLA\s
                    ELSE\s
                        CASE\s
                            WHEN ISNULL(D.ENFERMEIRA, 0) <> 0 THEN 'COREN' ELSE ''\s
                        END + ':' + LTRIM(D.COREN) + '-' + SIGLA\s
                END AS CONSELHO,\s
                CASE\s
                    WHEN A.TIPO = 1 THEN FIINT.FICHA ELSE FIAMB.FICHA\s
                END AS FICHA,\s
                CASE\s
                    WHEN A.TIPO = 1 THEN\s
                        CASE \s
                            WHEN LTRIM(RTRIM(ISNULL(FIINT.NOME_SOCIAL, ''))) <> '' THEN '[Ns] ' + UPPER(FIINT.NOME_SOCIAL)\s
                            ELSE FIINT.NOME\s
                        END\s
                    ELSE\s
                        CASE\s
                            WHEN LTRIM(RTRIM(ISNULL(FIAMB.NOME_SOCIAL, ''))) <> '' THEN '[Ns] ' + UPPER(FIAMB.NOME_SOCIAL)\s
                            ELSE FIAMB.NOME\s
                        END\s
                END AS NOME,\s
                CASE\s
                    WHEN A.TIPO = 1 THEN INT.DATAINTERNACAO ELSE AMB.DATAINTERNACAO\s
                END AS DATAINTERNACAO,\s
                CASE\s
                    WHEN A.TIPO = 1 THEN INT.DATAALTA ELSE AMB.DATAALTA\s
                END AS DATAALTA,\s
                ISNULL(UA.USUARIO, 0) AS USUARIO_AUTORIZADOR,\s
                ISNULL(UA.NOME, '') AS NOME_AUTORIZADOR,\s
                ISNULL(UT.DESCRICAO, '') AS TUTOR_AUTORIZADOR,\s
                CASE\s
                    WHEN ISNULL(UM.MEDICO, 0) <> 0 THEN\s
                        CASE\s
                            WHEN ISNULL(UM.CONSELHOPROFISSIONAL, 0) = 1 THEN 'CRAS'\s
                            WHEN ISNULL(UM.CONSELHOPROFISSIONAL, 0) = 2 THEN 'COREN'\s
                            WHEN ISNULL(UM.CONSELHOPROFISSIONAL, 0) = 3 THEN 'CRF'\s
                            WHEN ISNULL(UM.CONSELHOPROFISSIONAL, 0) = 4 THEN 'CRFA'\s
                            WHEN ISNULL(UM.CONSELHOPROFISSIONAL, 0) = 5 THEN 'CREFITO'\s
                            WHEN ISNULL(UM.CONSELHOPROFISSIONAL, 0) = 6 THEN 'CRM'\s
                            WHEN ISNULL(UM.CONSELHOPROFISSIONAL, 0) = 7 THEN 'CRV'\s
                            WHEN ISNULL(UM.CONSELHOPROFISSIONAL, 0) = 8 THEN 'CRN'\s
                            WHEN ISNULL(UM.CONSELHOPROFISSIONAL, 0) = 9 THEN 'CRO'\s
                            WHEN ISNULL(UM.CONSELHOPROFISSIONAL, 0) = 10 THEN 'CRP'\s
                            WHEN ISNULL(UM.CONSELHOPROFISSIONAL, 0) = 11 THEN 'OUT'\s
                            WHEN ISNULL(UM.CONSELHOPROFISSIONAL, 0) = 12 THEN 'CRBM'\s
                            WHEN ISNULL(UM.CONSELHOPROFISSIONAL, 0) = 13 THEN 'CRESS'\s
                        END + ':' + UM.CRM + '-' + SIGLA\s
                    ELSE\s
                        CASE\s
                            WHEN ISNULL(UE.ENFERMEIRA, 0) <> 0 THEN 'COREN' ELSE ''\s
                        END + ':' + LTRIM(UE.COREN) + '-' + SIGLA\s
                END AS REGISTRO_AUTORIZADOR,A.ASSINATURA\s
                FROM EVOLUCAO A\s
                LEFT JOIN USUARIO B ON A.USUARIO = B.USUARIO AND B.MEDICO <> 0\s
                LEFT JOIN MEDICOS C ON B.MEDICO = C.MEDICO\s
                LEFT JOIN ENFERMEIRA D ON A.USUARIO = D.USUARIO\s
                LEFT JOIN UF E ON C.UFCRM = E.ID_UF\s
                LEFT JOIN INTERNO INT ON A.REGISTRO = INT.REGISTRO\s
                LEFT JOIN FICHAS FIINT ON INT.FICHA = FIINT.FICHA\s
                LEFT JOIN AMBULATORIAL AMB ON A.REGISTRO = AMB.REGISTRO\s
                LEFT JOIN FICHAS FIAMB ON AMB.FICHA = FIAMB.FICHA\s
                LEFT JOIN USUARIO UA ON A.USUARIOAUTORIZADOR = UA.USUARIO\s
                LEFT JOIN MEDICOS UM ON UA.MEDICO = UM.MEDICO AND UM.MEDICO <> 0\s
                LEFT JOIN ENFERMEIRA UE ON A.USUARIOAUTORIZADOR = UE.USUARIO\s
                LEFT JOIN PRES_CAD_TUTOR UT ON UA.TUTORPRESCRICAO = UT.TUTOR\s
                LEFT JOIN PRES_CAD_TUTOR UTA ON A.TUTOR = UTA.TUTOR\s
                LEFT JOIN CENTROCUSTO CC ON A.CENTROCUSTO = CC.CentroCusto\s
                WHERE A.EVOLUCAO = \s""";

    public static final String SQL_EVOLUTION_HISTORY = " SELECT A.EVOLUCAO \n"
            + "	,A.REGISTRO \n"
            + "   , CASE WHEN A.TIPO = 1 THEN CASE WHEN LTRIM(RTRIM(ISNULL(FIINT.NOME_SOCIAL,''))) <> '' THEN  '[Ns] ' + UPPER(FIINT.NOME_SOCIAL) ELSE FIINT.NOME END ELSE CASE WHEN LTRIM(RTRIM(ISNULL(FIAMB.NOME_SOCIAL,''))) <> '' THEN  '[Ns] ' + UPPER(FIAMB.NOME_SOCIAL) ELSE FIAMB.NOME END END AS NOME  \n"
            + "	,A.DATA  ,A.USUARIO, A.HORA \n"
            + "	,A.IdSignedDocs as SIGNED_DOCS \n"
            + "   ,COALESCE(NULLIF(CAST(A.TEXTO AS VARCHAR(MAX)), ''), A.textoformatado) AS EVOLUCAO_TXT	             \n"
            + "   , ISNULL(C.NOME,'')   AS NOME_PROFISSIONAL						 \n"
            + "   , (CASE WHEN ISNULL(C.MEDICO,0)<>0 THEN							 \n"
            + "    (  CASE WHEN ISNULL(C.CONSELHOPROFISSIONAL,0)=1 THEN 'CRAS'	 \n"
            + "            WHEN ISNULL(C.CONSELHOPROFISSIONAL,0)=2 THEN 'COREN'	 \n"
            + "            WHEN ISNULL(C.CONSELHOPROFISSIONAL,0)=3 THEN 'CRF'		 \n"
            + "            WHEN ISNULL(C.CONSELHOPROFISSIONAL,0)=4 THEN 'CRFA'	 \n"
            + "            WHEN ISNULL(C.CONSELHOPROFISSIONAL,0)=5 THEN 'CREFITO'	 \n"
            + "            WHEN ISNULL(C.CONSELHOPROFISSIONAL,0)=6 THEN 'CRM'		 \n"
            + "            WHEN ISNULL(C.CONSELHOPROFISSIONAL,0)=7 THEN 'CRV'		 \n"
            + "            WHEN ISNULL(C.CONSELHOPROFISSIONAL,0)=8 THEN 'CRN'		 \n"
            + "            WHEN ISNULL(C.CONSELHOPROFISSIONAL,0)=9 THEN 'CRO'		 \n"
            + "            WHEN ISNULL(C.CONSELHOPROFISSIONAL,0)=10 THEN 'CRP'	 \n"
            + "            WHEN ISNULL(C.CONSELHOPROFISSIONAL,0)=11 THEN 'OUT'	 \n"
            + "            WHEN ISNULL(C.CONSELHOPROFISSIONAL,0)=12 THEN 'CRBM'	 \n"
            + "            WHEN ISNULL(C.CONSELHOPROFISSIONAL,0)=13 THEN 'CRESS'	 \n"
            + "            END) + ':' + C.CRM  + '-' + SIGLA 						 \n"
            + "     ELSE															 \n"
            + "            (CASE WHEN ISNULL(D.ENFERMEIRA,0)<>0 THEN 'COREN' ELSE '' END) + ':' + LTRIM(D.COREN)  + '-' + SIGLA  \n"
            + "      END) AS CONSELHO \n"
            + "	,   T.DESCRICAO AS TUTOR \n"
            + "   , CASE WHEN A.TIPO = 1 THEN FIINT.FICHA ELSE FIAMB.FICHA END AS PRONTUARIO  \n"
            + "   , CASE WHEN A.TIPO = 1 THEN INT.DATAINTERNACAO ELSE AMB.DATAINTERNACAO END AS DATAINTERNACAO  \n"
            + "   , CASE WHEN A.TIPO = 1 THEN INT.DATAALTA ELSE AMB.DATAALTA END AS DATAALTA  \n"
            + "   , ISNULL(UA.USUARIO,0) AS USUARIO_AUTORIZADOR  \n"
            + "   , ISNULL(UA.NOME,'')   AS NOME_AUTORIZADOR \n"
            + "   , ISNULL(UT.DESCRICAO,'') AS TUTOR_AUTORIZADOR  \n"
            + "   ,(CASE WHEN ISNULL(UM.MEDICO,0)<>0 THEN \n"
            + "    (  CASE WHEN ISNULL(UM.CONSELHOPROFISSIONAL,0)=1 THEN 'CRAS'     \n"
            + "            WHEN ISNULL(UM.CONSELHOPROFISSIONAL,0)=2 THEN 'COREN'	  \n"
            + "            WHEN ISNULL(UM.CONSELHOPROFISSIONAL,0)=3 THEN 'CRF'	  \n"
            + "            WHEN ISNULL(UM.CONSELHOPROFISSIONAL,0)=4 THEN 'CRFA'	  \n"
            + "            WHEN ISNULL(UM.CONSELHOPROFISSIONAL,0)=5 THEN 'CREFITO'  \n"
            + "            WHEN ISNULL(UM.CONSELHOPROFISSIONAL,0)=6 THEN 'CRM'	  \n"
            + "            WHEN ISNULL(UM.CONSELHOPROFISSIONAL,0)=7 THEN 'CRV'	  \n"
            + "            WHEN ISNULL(UM.CONSELHOPROFISSIONAL,0)=8 THEN 'CRN'	  \n"
            + "            WHEN ISNULL(UM.CONSELHOPROFISSIONAL,0)=9 THEN 'CRO'	  \n"
            + "            WHEN ISNULL(UM.CONSELHOPROFISSIONAL,0)=10 THEN 'CRP'	  \n"
            + "            WHEN ISNULL(UM.CONSELHOPROFISSIONAL,0)=11 THEN 'OUT'	  \n"
            + "            WHEN ISNULL(UM.CONSELHOPROFISSIONAL,0)=12 THEN 'CRBM'	  \n"
            + "            WHEN ISNULL(UM.CONSELHOPROFISSIONAL,0)=13 THEN 'CRESS'	  \n"
            + "            END) + ':' + UM.CRM  + '-' + SIGLA 					  \n"
            + "     ELSE															  \n"
            + "            (CASE WHEN ISNULL(UE.ENFERMEIRA,0)<>0 THEN 'COREN' ELSE '' END) + ':' + LTRIM(UE.COREN)  + '-' + SIGLA  \n"
            + "     END) AS CONSELHO_AUTORIZADOR \n"
            + " FROM EVOLUCAO A LEFT JOIN USUARIO B ON A.USUARIO=B.USUARIO AND B.MEDICO<>0 \n"
            + "               LEFT JOIN MEDICOS C ON B.MEDICO=C.MEDICO							 \n"
            + "               LEFT JOIN ENFERMEIRA D ON A.USUARIO=D.USUARIO					   \n"
            + "               LEFT JOIN UF E ON C.UFCRM = E.ID_UF							   \n"
            + "               LEFT JOIN INTERNO INT ON A.REGISTRO = INT.REGISTRO				   \n"
            + "               LEFT JOIN FICHAS FIINT ON INT.FICHA = FIINT.FICHA 				   \n"
            + "               LEFT JOIN AMBULATORIAL AMB ON A.REGISTRO = AMB.REGISTRO		   \n"
            + "               LEFT JOIN FICHAS FIAMB ON AMB.FICHA = FIAMB.FICHA 				   \n"
            + "               LEFT JOIN USUARIO UA ON A.USUARIOAUTORIZADOR = UA.USUARIO		   \n"
            + "               LEFT JOIN MEDICOS UM ON UA.MEDICO=UM.MEDICO  AND UM.MEDICO<>0	   \n"
            + "               LEFT JOIN ENFERMEIRA UE ON A.USUARIOAUTORIZADOR=UE.USUARIO		   \n"
            + "               LEFT JOIN PRES_CAD_TUTOR UT ON UA.TUTORPRESCRICAO = UT.TUTOR 	   \n"
            + "               LEFT JOIN PRES_CAD_TUTOR T ON B.TUTORPRESCRICAO = T.TUTOR 		   \n"
            + " WHERE ";

    public static final String SQL_LIST_TREATMENT = "    SELECT ATEND.TIPO AS TIPO_ATENDIMENTO		     \n"
                                                    + "    ,CASE WHEN ATEND.TIPO ='I' THEN 1			 \n"
                                                    + " 		 WHEN ATEND.TIPO ='A' THEN 2			 \n"
                                                    + " 		 WHEN ATEND.TIPO ='U' THEN 3			 \n"
                                                    + " 		 WHEN ATEND.TIPO ='E' THEN 4			 \n"
                                                    + " 	 ELSE 0 END AS IDTIPO_ATENDIMENTO 			 \n"
                                                    + "    ,CASE WHEN ATEND.TIPO ='I' THEN 'INT'		 \n"
                                                    + " 		 WHEN ATEND.TIPO ='A' OR ATEND.TIPO ='U' THEN 'AMB'			 \n"
                                                    + " 		 WHEN ATEND.TIPO ='E' THEN 'EXT'			 \n"
                                                    + " 	 ELSE '' END AS TP_ATENDIMENTO 			 \n"
                                                    + "    ,CASE WHEN ATEND.TIPO ='I' THEN 'INTERNO'		 \n"
                                                    + " 		 WHEN ATEND.TIPO ='A' OR ATEND.TIPO ='U' THEN 'AMBULATORIAL'			 \n"
                                                    + " 		 WHEN ATEND.TIPO ='E' THEN 'EXTERNO'			 \n"
                                                    + " 	 ELSE '' END AS TAB_ATENDIMENTO 			 \n"
                                                    + "  , 99  AS CONFERIDO 							 \n"
                                                    + "  , FI.HYGIA  AS HYGIA                                    \n"
                                                    + "  , FI.CPF   AS CPF                                    \n"
                                                    + "  , ISNULL(ATEND.LEITO,'') AS LEITO				 \n"
                                                    + "  , ISNULL(CC.DESCRICAO,'') AS DESCRICAOCC		 \n"
                                                    + "  , ATEND.REGISTRO AS ATENDIMENTO					 \n"
                                                    + "  , ATEND.REGISTRO AS ATEDIMENTO					 \n"
                                                    + "  , ATEND.FICHA AS PRONTUARIO					 \n"
                                                    + "  , FI.NOME AS NOME_PACIENTE 					 \n"
                                                    + "  , FI.NOMEMAE AS NOME_MAE 						 \n"
                                                    + "  , ISNULL(ATEND.MEDICO_BLOQUEIO,0) AS MEDICO_BLOQUEIO \n"
                                                    + "  , ATEND.DATA_ATENDIMENTO  \n"
                                                    + "  ,  FORMAT(FLOOR(CAST(ISNULL(ATEND.DATA_ALTA,GETDATE())-ISNULL(ATEND.DATA_ATENDIMENTO,GETDATE()) AS FLOAT)),'0d ') + FORMAT( (ISNULL(ATEND.DATA_ALTA,GETDATE())-ISNULL(ATEND.DATA_ATENDIMENTO,GETDATE())) ,'hh:mm')  AS TEMPO_ATENDIMENTO \n"
                                                    + "  , ATEND.DATA_ALTA \n"
                                                    + "  , ATEND.CONVENIO \n"
                                                    + "  , CONV.TIPOCONVENIO \n"
                                                    + "  , CONV.DESCRICAO AS NOME_CONVENIO \n"
                                                    + "  , FI.NASCIMENTO  \n"
                                                    + "  , CAST(FI.ALERGIA AS VARCHAR(MAX)) AS ALERGIA  \n"
                                                    + "  , CC.TIPO as TIPO_SETOR  \n"
                                                    + "  --, ATENDIMENTO.TEMPO_SETOR AS TEMPO_SETOR \n"
                                                    + "  , 0 AS TEMPO_SETOR \n"
                                                    + "  , ISNULL(ATEND.CENTROCUSTO_ATUAL, ATEND.CENTROCUSTO) AS  SETOR  \n"
                                                    + "  , 0 AS EVOLUCAO   \n"
                                                    + "  , ISNULL(ATEND.CID,'') AS CID  \n"
                                                    + "  , 0  AS STATUS_LAUDO  \n"
                                                    + "  , 0 AS ANAMNESE  \n"
                                                    + "  , ISNULL(ALTAS.TIPOALTA,0) AS TIPOSAIDA   \n"
                                                    + "  , 0 AS INSUMO  \n"
                                                    + "  , ATEND.DATA_ALTA \n"
                                                    + "  , ATEND.MEDICO  \n"
                                                    + "  , ATEND.TEMPO_CONSULTA  \n"
                                                    + "  , ISNULL(ATEND.CLASSIFICACAO,99) AS CLASSIFICACAO  \n"
                                                    + "  , ISNULL(ATEND.URGENCIA,0) AS URGENCIA				 \n"
                                                    + "  , MED.NOME AS NOMEMEDICO						   \n"
                                                    + "  , ISNULL(ATEND.ESPECMEDICA, 0) AS ESPECMEDICA	   \n"
                                                    + "  , ATEND.HORA_TRIAGEM							   \n"
                                                    + "  , ATEND.HORA_PRESCRICAO  						   \n"
                                                    + "  , DATEDIFF(MINUTE,ISNULL(ATEND.HORA_TRIAGEM,GETDATE()),ISNULL(ATEND.HORA_PRESCRICAO,GETDATE())) AS TEMPO_TRIAGEM  \n"
                                                    + "  , ISNULL(CC.RADIOLOGIA,0) AS RADIOLOGIA \n"
                                                    + "  , ISNULL(CC.FISIOTERAPIA,0) AS FISIOTERAPIA \n"
                                                    + "  , ISNULL(CC.ENDOSCOPIADIGESTIVA,0) AS ENDOSCOPIADIGESTIVA \n"
                                                    + "  FROM	V_ATENDIMENTO ATEND \n"
                                                    + " 		INNER JOIN FICHAS                           FI   ON ATEND.FICHA = FI.FICHA  \n"
                                                    + " 		LEFT  JOIN CENTROCUSTO                      CC   ON ISNULL(ATEND.CENTROCUSTO_ATUAL, ATEND.CENTROCUSTO) = CC.CENTROCUSTO  \n"
                                                    + " 		LEFT  JOIN MEDICOS                          MED ON ATEND.MEDICO = MED.MEDICO  \n"
                                                    + "         LEFT  JOIN ALTAS                            ALTAS ON ATEND.REGISTRO = ALTAS.REGISTRO  \n"
                                                    + " 		INNER JOIN CONVENIOS                        CONV ON ATEND.CONVENIO = CONV.CONVENIO \n"
                                                    + "  WHERE ((EXISTS  ( SELECT * FROM USUARIOCENTROCUSTOAUTORIZACAO B WITH (NOLOCK)   \n"
                                                    + "                 WHERE ATEND.CENTROCUSTO = B.CENTROCUSTO  \n"
                                                    + "                 AND B.USUARIO =  \n";

    public static final String SQL_LIST_TREATMENT_WHERE_INTER = "      AND B.MOVIMENTA = 1   \n"
                                                                + " 		))) \n"
                                                                + "  AND ATEND.DATA_ALTA IS NULL   \n";

    public static final String SQL_LIST_TREATMENT_GROUP = " GROUP BY  ATEND.TIPO,ATEND.CENTROCUSTO,ATEND.LEITO, ATEND.REGISTRO, ATEND.FICHA, FI.NOME, ATEND.TEMPO_CONSULTA  \n"
                                        + " 			,ATEND.DATA_ATENDIMENTO, FI.HYGIA, FI.CPF,  ATEND.DATA_ALTA, ATEND.CONVENIO, CONV.TIPOCONVENIO, FI.NASCIMENTO, CAST(FI.ALERGIA AS VARCHAR(MAX)), CC.DESCRICAO, ATEND.CENTROCUSTO_ATUAL \n"
                                        + " 			,ATEND.LEITO  , CONV.DESCRICAO ,CC.ENDOSCOPIADIGESTIVA,ATEND.CID, CC.TIPO, ATEND.MEDICO_BLOQUEIO, ALTAS.TIPOALTA, ATEND.DATA_ALTA \n"
                                        + " 			, ATEND.MEDICO,ATEND.HORA_TRIAGEM,ATEND.HORA_PRESCRICAO, ATEND.CLASSIFICACAO, ATEND.URGENCIA,MED.NOME, CC.CENTROCUSTO, ATEND.ESPECMEDICA \n"
                                        + " 			, ATEND.CENTROCUSTO,CC.RADIOLOGIA,CC.FISIOTERAPIA, FI.NOMEMAE,CC.DESCRICAO  \n"
                                        + "   ORDER BY ATEND.CLASSIFICACAO ASC,ATEND.HORA_TRIAGEM ASC, ATEND.DATA_ATENDIMENTO ASC  \n";

    public static final String SQL_LIST_THERAPEUTIC_PLANNING = " SELECT A.PRESCRICAO, L.DESCRICAO AS INTERVALO, C.NECESSARIO,C.VIAADMINISTRATIVO,C.SUSPENSO,C.DATASUSPENSAO,C.HORASUSPENSAO,C.USUARIOSUSPENSAO,M.NOME,K.DESCRICAO AS VIA, A.REGISTRO, " +
                                                                "A.PROCEDIMENTO, C.PROCEDIMENTONOME, A.SEQUENCIA, LEFT(A.HORA,2) + ':00' AS HORA, A.DATA,COUNT(A.HORA) AS QUANTIDADE,\n" +
                                                                "        ISNULL(A.MEDICACAO_ADMINISTRADA,0) AS MEDICACAO_ADMINISTRADA, SUM(ISNULL(A.QUANTIDADEINDIVIDUAL, 0)) AS QUANTIDADEINDIVIDUAL, \n" +
                                                                "        ISNULL(A.ATUALIZACAO,'') AS ATUALIZACAO, CASE WHEN ISNULL(A.OBSNAOENVIADO,'') = '' THEN '' ELSE  ' | OBS FARMÁCIA: ' + ISNULL(A.OBSNAOENVIADO,'') END  AS OBSNAOENVIADO, C.OBSERVACAOPRESCRICAO  AS OBSERVACAOPRESCRICAO \n" +
                                                                " FROM PREELETPROCEDIMENTOENFERMAGEM_";

    public static final String SQL_LIST_THERAPEUTIC_PLANNING_INNER =  " C ON A.REGISTRO = C.REGISTRO  \n" +
                                                                    "    AND A.PROCEDIMENTO = C.PROCEDIMENTO  \n" +
                                                                    "     AND A.SEQUENCIA = C.SEQUENCIA  \n" +
                                                                    " LEFT JOIN PRES_CAD_INTERVALO L  (NOLOCK) ON C.INTERVALO = L.INTERVALO \n" +
                                                                    " LEFT JOIN PRES_CAD_VIA K  (NOLOCK) ON C.VIAADMINISTRATIVO = K.VIA \n" +
                                                                    " LEFT JOIN USUARIO M  (NOLOCK) ON C.USUARIODISPENSACAO = M.USUARIO ";

    public static final String  SQL_LIST_THERAPEUTIC_PLANNING_GROUP =  " GROUP BY C.OBSERVACAOPRESCRICAO, C.PROCEDIMENTONOME, A.REGISTRO, A.PROCEDIMENTO,A.OBSNAOENVIADO, A.SEQUENCIA, LEFT(A.HORA,2) \n" +
                                                                    " ,A.DATA,A.MEDICACAO_ADMINISTRADA,A.ATUALIZACAO,L.DESCRICAO,C.NECESSARIO,C.VIAADMINISTRATIVO \n" +
                                                                    " ,C.SUSPENSO,C.DATASUSPENSAO,C.HORASUSPENSAO,C.USUARIOSUSPENSAO,M.NOME,K.DESCRICAO, A.PRESCRICAO \n" ;

    public static final String SQL_LIST_THERAPEUTIC_PLANNING_UNION_ALL_1 = " UNION ALL \n" +
                                                                    " SELECT C.NOTA AS PRESCRICAO, L.DESCRICAO,C.NECESSARIO,C.VIAADMINISTRATIVO,C.SUSPENSO,C.DATASUSPENSAO,C.HORASUSPENSAO,C.USUARIOSUSPENSAO,M.NOME, " +
                                                                    "K.DESCRICAO,C.REGISTRO, C.PROCEDIMENTO, C.PROCEDIMENTONOME, C.SEQUENCIA, NULL AS HORA, C.DATA,C.QUANTIDADE AS QUANTIDADE,\n" +
                                                                    "        0 AS MEDICACAO_ADMINISTRADA, C.QUANTIDADE  AS QUANTIDADEINDIVIDUAL, \n" +
                                                                    "        ISNULL(C.ATUALIZACAO,'') AS ATUALIZACAO, CASE WHEN ISNULL(C.OBSNECESSARIO ,'') = '' THEN '' ELSE  ' | OBS SE NECESSÁRIO: ' + ISNULL(C.OBSNECESSARIO,'') END  AS OBSNAOENVIADO, C.OBSERVACAOPRESCRICAO  AS OBSERVACAOPRESCRICAO \n" +
                                                                    " FROM   MOVIM_PRES_INT C \n" +
                                                                    "   LEFT JOIN PRES_CAD_INTERVALO L  (NOLOCK) ON C.INTERVALO = L.INTERVALO\n " +
                                                                    "   LEFT JOIN PRES_CAD_VIA K  (NOLOCK) ON C.VIAADMINISTRATIVO = K.VIA\n " +
                                                                    "   LEFT JOIN USUARIO M  (NOLOCK) ON C.USUARIODISPENSACAO = M.USUARIO ";

    public static final String SQL_TUTOR_LIST = "SELECT TUTOR, DESCRICAO  FROM PRES_CAD_TUTOR  \n" +
                                                " WHERE  TUTOR IN( SELECT ISNULL(TUTORPERMISSAO,0) AS TUTORPERMISSAO \n" +
                                                " FROM PRES_TUTOR_EVOLUCAO \n" +
                                                " WHERE TUTOR = 1 AND ISNULL(CONSULTAR,0) = 1 ) \n" +
                                                " AND ISNULL(OCULTAR,0)=0 \n" +
                                                " UNION ALL \n" +
                                                " SELECT 9999 AS TUTOR, 'EVOLUÇÕES AGRUPADAS' AS DESCRICAO \n" +
                                                " UNION ALL \n" +
                                                " SELECT 9998 AS TUTOR, 'AUXILIAR/TECNICO DE ENFERMAGEM' AS DESCRICAO \n" +
                                                " ORDER BY 2";

    public static final String SQL_DATE_INIT = " WITH Intervalos AS (   \n" +
                                      " SELECT \n" +
                                      "     CAST(HoraInicio AS TIME) AS Horario, \n" +
                                      "     CAST(HoraFim AS TIME) AS HoraFim, \n" +
                                      "     intervaloMinutos, \n" +
                                      " CENTROCUSTOID \n" +
                                      " FROM GradeHorario \n" +
                                      " WHERE diasemana = DATEPART(WEEKDAY, '";
    public static final String SQL_DATE_CENTRO = "') AND CENTROCUSTOID = ";
    public static final String SQL_DATE_MEDICO = " \n AND MedicoID = ";
    public static final String SQL_DATA_SECOND = " \n UNION ALL  SELECT \n" +
                                                 "DATEADD(MINUTE, intervaloMinutos, Horario), \n" +
                                                 " HoraFim, \n" +
                                                 " intervaloMinutos, \n" +
                                                 " CENTROCUSTOID FROM Intervalos \n" +
                                                  " WHERE DATEADD(MINUTE, intervaloMinutos, Horario) <= HoraFim ) \n" +
                                                  " SELECT I.Horario, I.intervaloMinutos FROM Intervalos I \n" +
                                                  " WHERE NOT EXISTS ( \n" +
                                                  " SELECT 1  FROM Agendamento A \n" +
                                                  " WHERE I.Horario >= CAST(A.HoraInicio AS TIME) \n" +
                                                  " AND I.Horario < CAST(A.HoraTermino AS TIME) \n" +
                                                  " AND A.CentroCustoID = I.CENTROCUSTOID ";

    public static final String SQL_DOCTOR_FIND_PART_1 = "SELECT MEDICOS.MEDICO, MEDICOS.NOME + ' - ' + ESPMEDICA.DESCRICAO  AS DESCRICAO \n" +
                                                        " FROM MEDICOS MEDICOS INNER JOIN ESPMEDICA ESPMEDICA ON MEDICOS.ESPECMEDICA = ESPMEDICA.ESPECMEDICA \n" +
                                                        " WHERE ISNULL(CANCELADO,0) = 0 \n" +
                                                        " AND  CLASSIFICACAO NOT IN(SELECT CLASSIFICACAO FROM CLASSIFICACOES WHERE ISNULL(NAOINTEGRACORPOCLINICO,0)=1 ) \n" +
                                                        " AND Medicos.nome LIKE '";

    public static final String SQL_DOCTOR_FIND_PART_2 = "%' \n" +
                                    "GROUP BY MEDICOS.MEDICO,ESPMEDICA.DESCRICAO,MEDICOS.NOME \n" +
                                    "ORDER BY MEDICOS.NOME";

    public static final String SQL_MAPS_SCHEDULER = "SELECT A.AGENDAMENTOID, A.TIPOAGENDAMENTO \n" +
                                    ",B.DESCRICAO+ ' - ' + CAST(b.CENTROCUSTO AS VARCHAR) AS CENTROCUSTO\n" +
                                    ",I.NOME + ' - ' + CAST(I.MEDICO AS VARCHAR)  AS PROFISSIONAL\n" +
                                    ",A.Ficha\n" +
                                    ",A.Paciente \n" +
                                    ",A.NASCIMENTO\n" +
                                    ",A.HORAINICIO AS DATA\n" +
                                    " ,DATEDIFF(MINUTE ,A.HORAINICIO,A.HORATERMINO) AS TEMPO \n" +
                                    ",J.LOGON AS USUARIO\n" +
                                    ",A.Contato1\n" +
                                    ",A.Contato2\n" +
                                    ",CASE WHEN A.whatsapp = 1 THEN 'SIM' ELSE 'NÃO' END AS whatsapp\n" +
                                    ",A.Observacoes \n" +
                                    ",A.Status\n" +
                                    ",CASE WHEN A.ENCAIXEHORARIO = 1 THEN 'SIM' ELSE 'NÃO' END AS ENCAIXE \n" +
                                    ",H.codigo AS COD_RECURSO\n" +
                                    ",H.Nome AS NOME_RECURSO\n" +
                                    ",G.Quantidade AS QUANT_RECURSO\n" +
                                    ",K.FUNCAO AS FUNCAO\n" +
                                    ",L.NOME + ' - ' + CAST(L.MEDICO AS VARCHAR)  AS PROFISSIONAL_EQUIPE\n" +
                                    ",C.DESCRICAO AS CONVENIO"+
                                    "FROM AGENDAMENTO A \n" +
                                    "INNER JOIN CENTROCUSTO B ON A.CENTROCUSTOID=B.CENTROCUSTO\n" +
                                    "LEFT  JOIN CONVENIOS C ON A.CONVENIOID=C.CONVENIO \n" +
                                    "LEFT  JOIN USUARIO D ON A.SECRETARIAID=D.USUARIO  \n" +
                                    "LEFT  JOIN FICHAS F ON A.FICHA=F.FICHA\n" +
                                    "LEFT  JOIN AgendamentoRecurso G ON A.AgendamentoID=G.AgendamentoID\n" +
                                    "LEFT  JOIN RECURSO H ON G.RecursoID=H.RecursoID \n" +
                                    "INNER  JOIN MEDICOS I ON A.MEDICOID=I.MEDICO \n" +
                                    "LEFT  JOIN USUARIO  J ON A.SecretariaID=J.USUARIO \n" +
                                    "LEFT  JOIN EquipeAgendamento K ON A.AgendamentoID=K.AgendamentoID\n" +
                                    "LEFT  JOIN MEDICOS L ON K.MEDICOID=L.MEDICO \n" +
                                    "WHERE 1=1\n";

}
