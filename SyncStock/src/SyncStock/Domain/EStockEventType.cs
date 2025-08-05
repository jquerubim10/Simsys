using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SyncStock.Domain
{
    public enum EStockEventType
    {
        //Eventos com códigos pares são as operações, eventos impares são as operações inversas
        ENTRADA_SEM_PEDIDO = 10,
        DEV_ENTRADA_SEM_PEDIDO = 11,
        ENTRADA_POR_PEDIDO = 12,
        DEV_ENTRADA_POR_PEDIDO = 13,
        EMPRESTIMO_SAIDA = 20,
        DEV_EMPRESTIMO_SAIDA = 21,
        EMPRESTIMO_ENTRADA = 22,
        DEV_EMPRESTIMO_ENTRADA = 23,
        CONSUMO_SETOR = 30,
        DEV_CONSUMO_SETOR = 31,
        CONSUMO_PACIENTE = 40,
        DEV_CONSUMO_PACIENTE = 41,
        ACERTO_ESTOQUE_ENTRADA = 50,
        ACERTO_ESTOQUE_SAIDA = 52
    }
}
