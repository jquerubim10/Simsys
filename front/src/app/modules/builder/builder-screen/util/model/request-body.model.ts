

export class RequestBody {
    tableName?: string | '';
    tabelNameFull?: string | '';
    columnName?: string | '';
    operationType?: string | '';
    whereValue?: string | '';
    valuesInsert?: string | '';
    valuesUpdate?: string | '';
    selectOne?: string | '';
    tutorUser?: string | '';
    arrayCenter?: string | '';

    constructor(item?: RequestBody) {
        this.tableName = item?.tableName || null;
        this.tabelNameFull = item?.tabelNameFull || null;
        this.columnName = item?.columnName || null;
        this.operationType = item?.operationType || null;
        this.whereValue = item?.whereValue || null;
        this.valuesInsert = item?.valuesInsert || null;
        this.valuesUpdate = item?.valuesUpdate || null;
        this.selectOne = item?.selectOne || null;
        this.tutorUser = item?.tutorUser || null;
        this.arrayCenter = item?.arrayCenter || null;
    }
}
