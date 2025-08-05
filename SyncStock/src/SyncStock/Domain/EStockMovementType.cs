using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SyncStock.Domain
{
    public enum EStockMovementType
    {
        INPUT_MOVIM = 1,
        NEUTRAL_MOVIM = 0,
        OUTPUT_MOVIM = -1
    }
}
