import { DivBuilder } from "./div.model";


export class ScreenBuilder {
	id: number | null;
	title: string;
	icon: string | null;
	dateCreated?: string | null;
	tableName: string;
	typeTab?: string | null;

    divs: any[] | [];

	previewOtherScreen?: boolean | false;

	active?: boolean | false;
	screenSign?: boolean | false;
	screenPreview?: boolean | false;
	screenLogin?: boolean | false;
	executeOption?: boolean | false;

	preSaveListTxt?: string | null;
	preSaveFunctionListTxt?: string | null;
	proSaveListTxt?: string | null;

	/**
	 * Constructor
	 */
	constructor(item: ScreenBuilder) {
		this.id = item.id || null;
		this.title = item.title;
		this.icon = item.icon || null;
		this.dateCreated = item.dateCreated || null;
		this.tableName = item.tableName || null;

        this.divs = item.divs || [];

		this.previewOtherScreen = item.previewOtherScreen || false;
		this.typeTab = item.typeTab || null;

		this.active = item.active || false;
		this.screenSign = item.screenSign || false;
		this.screenPreview = item.screenPreview || false;
		this.screenLogin = item.screenLogin || false;
		this.executeOption = item.executeOption || false;

		this.preSaveListTxt = item.preSaveListTxt || null;
		this.preSaveFunctionListTxt = item.preSaveFunctionListTxt || null;
		this.proSaveListTxt = item.proSaveListTxt || null;
	}
}
