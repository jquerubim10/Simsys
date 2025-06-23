// -----------------------------------------------------------------------------------------------------
// @ REQUEST BODY

// -----------------------------------------------------------------------------------------------------
export interface RequestBody {

    tableName: string | null;
    columnName: string | null;
    operationType: string | null;
    whereValue: string | null;
    valuesInsert: string | null;
    valuesUpdate: string | null;

}
