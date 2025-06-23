export class Field {
	id?: number;
    placeholder?: string;
    type?: string;
    label?: string;
    formControlName?: string;
    
    lines?: number | 0;
    orderScreen?: number;
    orderRow?: number;
    value?: any;
    
    css?: string;
    className?: string;
    columnName?: string;
    mask?: string;

    infoSensitive?: boolean | false;
    searchable?: boolean | false;
    editable?: boolean | false;
    visible?: boolean | true;
    previewOnly?: boolean | false;

    validatorRequired?: boolean | false;
    validatorMin?: number | 10;
    validatorMax?: number | 255;
    validatorMessage?: string | 'No Message Register';

    functionMethod?: string | '';

    sqlText?: string | '';
    sqlList?: any[] | [];

    sqlObjText?: string | '';
    names?: any[] | [];
    isLoadingSelect?: boolean | false;

    divId: number;
    screenId: number;

    active?: boolean | false;
    defaultsValue?: boolean | false;
    nullValue?: boolean | false;

	/**
	 * Constructor
	 */
	constructor(item: Field) {
		this.id = item.id || null;
		this.placeholder = item.placeholder;
		this.type = item.type || null;
		this.label = item.label || null;
		this.formControlName = item.formControlName || null;

		this.lines = item.lines || 0;
		this.value = item.value || null;

		this.css = item.css || null;
		this.className = item.className || null;

		this.columnName = item.columnName || null;
		this.mask = item.mask || null;

        this.infoSensitive = item.infoSensitive || false;
        this.searchable = item.searchable || false;
        this.editable = item.editable || false;
        this.visible = item.visible || true;
        this.nullValue = item.nullValue || false;
        this.previewOnly = item.previewOnly || false;

        this.validatorRequired = item.validatorRequired || false;
        this.validatorMin = item.validatorMin || 10;
        this.validatorMax = item.validatorMax || 255;
        this.validatorMessage = item.validatorMessage || 'No Message Register';



        this.functionMethod = item.functionMethod || '';

        this.sqlText = item.sqlText || '';
        this.sqlList = item.sqlList || [];

        this.sqlObjText = item.sqlObjText || '';
        this.names = item.names || [];
        this.isLoadingSelect =  item.isLoadingSelect || false;

		this.divId = item.divId;
		this.screenId = item.screenId;
		this.defaultsValue = item.defaultsValue;


		this.active = item.active || false;
	}
}
