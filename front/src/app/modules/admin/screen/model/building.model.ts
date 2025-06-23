import { di } from "@fullcalendar/core/internal-common";
import { DivBuilder } from "./div.model";
import { Field } from "./field.model";


export class BuildingScreen {
	id: number | null;
	title: string;
	icon: string | null;
	dateCreated?: string | null;
	tableName: string;

	previewObjectText?: string | null;

    divJson?: string;
    divs: DivBuilder[];

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
	constructor(item: BuildingScreen) {
		this.id = item.id || null;
		this.title = item.title;
		this.icon = item.icon || null;
		this.dateCreated = item.dateCreated || null;
		this.tableName = item.tableName || null;

        this.divJson = item.divJson || null;

        this.divs = JSON.parse(item.divJson) || [];
        this.divs.forEach((div) => {
            div.fields = JSON.parse(div.fieldJson);
        })

		this.previewOtherScreen = item.previewOtherScreen || false;

		this.previewObjectText = item.previewObjectText || null;

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