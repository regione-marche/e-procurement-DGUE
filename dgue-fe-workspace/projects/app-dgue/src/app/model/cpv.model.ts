export interface TabellatoCpv { 
	text?: string;
	cod1?: string; 
	cod2?: string;
	cod3?: string;
	cod4?: string;
	cod?: string;
	data?: string;
	children?: Array<TabellatoCpv>;
	expandedIcon: string;
    collapsedIcon: string;

}