// -----------------------------------------------------------------------------------------------------
// @ PAINEL

import { IPainel } from "../types/painel.types";


// -----------------------------------------------------------------------------------------------------
export class Painel implements Required<IPainel>
{
    id: string | null;
    title: string;
    description: string | null;
    icon: string | null;
    url: string | null;
    active: boolean | false;

    /**
     * Constructor
     */
    constructor(board: IPainel) {
        this.id = board.id || null;
        this.title = board.title;
        this.description = board.description || null;
        this.icon = board.icon || null;
        this.url = board.url || null;
        this.active = board.active || false
    }
}
