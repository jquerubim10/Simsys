import { Field } from "./field.model";

export class DivBuilder {
	id: number | null;
	title: string;

    screenId: number | null;

    fieldJson?: string;

    fields: any[];

    active?: boolean | false;

    constructor(item: DivBuilder) {
        this.id = item.id,
        this.title = item.title

        this.fieldJson = item.fieldJson

        this.fields = [];

        this.screenId = item.screenId

        this.active = item.active;
    }
}