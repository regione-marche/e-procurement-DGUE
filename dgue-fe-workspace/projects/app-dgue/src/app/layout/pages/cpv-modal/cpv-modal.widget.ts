import {
    AfterViewInit,
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    ElementRef,
    Inject,
    Injector,
    OnDestroy,
    OnInit,
    ViewChild,
} from '@angular/core';
import {
    SDK_APP_CONFIG,
    SdkAbstractComponent,
    SdkAppEnvConfig,
    SdkHttpLoaderService,
    SdkHttpLoaderType,
    SdkStoreService,
    UserProfile,
} from '@maggioli/sdk-commons';
import {
    SdkBasicButtonInput,
    SdkButtonGroupInput,
    SdkButtonGroupOutput,
    SdkFormBuilderConfiguration,
    SdkSimpleSearchConfig,
    SdkSimpleSearchInput,
    SdkTextboxConfig,
    SdkTextInput,
    SdkTreeConfig,
} from '@maggioli/sdk-controls';
import { TranslateService } from '@ngx-translate/core';
import { clone, each, get, has, head, isArray, isEmpty, isObject, set, split } from 'lodash-es';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { TabellatoCpv } from '../../../model/cpv.model';
import { CpvCostants } from '../../../utils/cpvConstants';

@Component({
    templateUrl: `cpv-modal.widget.html`,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CpvModalWidget extends SdkAbstractComponent<any, void, any> implements OnInit, AfterViewInit, OnDestroy {

    // #region Variables

    public config: any;
    public data: any;
    public originalData: any;
    public buttons: SdkButtonGroupInput;
    public buttonsSubj: BehaviorSubject<SdkButtonGroupInput>;


    public userProfile: UserProfile;
    public dataConfig: Subject<SdkTreeConfig> = new Subject();

    public cpvConfigurationObs: Observable<SdkTextboxConfig> = of({
        code: 'calculated',
        label: 'CPV.CALCULATED',
        disabled: true
    } as SdkTextboxConfig);

    public calculateDataSubject: Subject<SdkTextInput> = new Subject();
    public currentCpv: string;
    public selectedItem: string;
    public actualNode: any;
    public searchConfig = of({ code: 'search', placeholder: this.translateService.instant('CPV.PLACEHOLDER') } as SdkSimpleSearchConfig);
    public searchData = of({ data: '' } as SdkSimpleSearchInput);
    public searchResult: Array<any> = [];

    public firstLevel: Array<any> = [];
    public secondLevel: Array<any> = [];
    public thirdLevel: Array<any> = [];
    public fourthLevel: Array<any> = [];
    public buttonCleanConfig: Observable<SdkBasicButtonInput> = of(
        {
            code: 'clean',
            label: '',
            disabled: false,
            title: 'BUTTONS.CLOSE',
            icon: 'mgg-icons-crud-cancel',
            type: 'functions',
            look: 'icon'
        } as SdkBasicButtonInput
    );

    @ViewChild('messages') _messagesPanel: ElementRef;

    // #endregion

    constructor(inj: Injector, cdr: ChangeDetectorRef, @Inject(SDK_APP_CONFIG) private appConfig: SdkAppEnvConfig) { super(inj, cdr) }

    // #region Hooks

    protected onAfterViewInit(): void {
        let result: Array<TabellatoCpv> = CpvCostants.cpv;
        this.data = result;
        this.populateData();
        if (!isEmpty(this.selectedItem)) {
            this.currentCpv = this.selectedItem;
        }
    }

    protected onInit(): void {
        this.loadButtons();
    }

    protected onDestroy(): void { }

    protected onConfig(config: any): void {
        if (isObject(config)) {
            this.markForCheck(() => {
                this.config = { ...config };
                this.config.label = 'PROVA';
                this.selectedItem = this.config.selectedItem;
                this.isReady = true;
            });
        }
    }

    protected onData(_data: void): void { }

    protected onUpdateState(_state: boolean): void { }

    // #endregion

    // #region Getters

    private get cpvCostants(): CpvCostants { { return this.injectable(CpvCostants) } }

    private get sdkHttpLoaderService(): SdkHttpLoaderService { return this.injectable(SdkHttpLoaderService) }

    private get store(): SdkStoreService { return this.injectable(SdkStoreService) }

    private get translateService(): TranslateService { return this.injectable(TranslateService) }

    // #endregion

    // #region Private
    private populateData(): void {

        //this.markForCheck(() => {
        this.populateLevels();
        if (!isEmpty(this.selectedItem)) {
            let parts: Array<string> = split(this.selectedItem, '-');

            let actualCode: string;
            if (isArray(parts)) {
                actualCode = head(parts);
                while (actualCode.endsWith('00')) {
                    actualCode = actualCode.substring(0, actualCode.length - 2);
                }
            }
            let existingCod1: string = actualCode.substring(0, 2);
            let existingCod2: string = actualCode.length > 3 ? actualCode.substring(2, 4) : null;
            let existingCod3: string = actualCode.length > 5 ? actualCode.substring(4, 6) : null;
            let existingCod4: string = actualCode.length > 5 ? actualCode.substring(6, 8) : null;
            for (let i = 0; i < this.firstLevel.length; i++) {
                if (this.firstLevel[i].cod1 === existingCod1) {
                    if (existingCod2 != null) {
                        this.firstLevel[i].expanded = true;
                        for (let j = 0; j < this.firstLevel[i].children.length; j++) {
                            if (this.firstLevel[i].children[j].cod2 === existingCod2) {
                                if (existingCod3 != null) {
                                    this.firstLevel[i].children[j].expanded = true;
                                    for (let x = 0; x < this.firstLevel[i].children[j].children.length; x++) {
                                        if (this.firstLevel[i].children[j].children[x].cod3 === existingCod3) {
                                            if (existingCod4 != null) {
                                                this.firstLevel[i].children[j].children[x].expanded = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            this.calculateDataSubject.next({
                data: this.selectedItem
            });
        }

        this.dataConfig.next({
            code: 'tree',
            items: this.firstLevel,
            selectedValue: this.actualNode
        });
        this.originalData = clone(this.data);
        //});

    }

    private populateLevels() {

        this.firstLevel = new Array<any>();
        this.secondLevel = new Array<any>();
        this.thirdLevel = new Array<any>();
        this.fourthLevel = new Array<any>();

        each(this.data, (node: TabellatoCpv) => {

            node.expandedIcon = "mgg-icons-file-folder-open";
            node.collapsedIcon = "mgg-icons-file-folder";
            node.data = node.cod;

            if (node.cod2 === '00') {
                if (!isEmpty(this.selectedItem)) {
                    if (node.cod === this.selectedItem) {
                        this.actualNode = node;
                    }
                }

                this.firstLevel.push(node);

            } else if (node.cod3 === '00') {
                this.secondLevel.push(node);
                if (!isEmpty(this.selectedItem)) {
                    if (node.cod === this.selectedItem) {
                        this.actualNode = node;
                    }
                }
            }
            else if (node.cod4 === '00') {
                this.thirdLevel.push(node);
                if (!isEmpty(this.selectedItem)) {
                    if (node.cod === this.selectedItem) {
                        this.actualNode = node;
                    }
                }
            }
            else {
                this.fourthLevel.push(node);
                if (!isEmpty(this.selectedItem)) {
                    if (node.cod === this.selectedItem) {
                        this.actualNode = node;
                    }
                }
            }
        });
        each(this.firstLevel, (node1: TabellatoCpv) => {

            let itemsSecondLevel: Array<TabellatoCpv> = [];

            each(this.secondLevel, (node2: TabellatoCpv) => {

                if (node2.cod1 === node1.cod1) {

                    itemsSecondLevel.push(node2);
                }
            });
            each(itemsSecondLevel, (item2: TabellatoCpv) => {
                let itemsThirdLevel: Array<TabellatoCpv> = [];

                each(this.thirdLevel, (node3) => {
                    if ('60112000-6' === node3.cod) {
                        if (item2.cod.startsWith('6010')) {
                            itemsThirdLevel.push(node3);
                        }
                    }
                    if (node3.cod2 === item2.cod2 && node3.cod1 === item2.cod1) {
                        itemsThirdLevel.push(node3)
                    }
                });
                each(itemsThirdLevel, (item3: TabellatoCpv) => {
                    let itemsFourthLevel: Array<TabellatoCpv> = [];
                    each(this.fourthLevel, (node4) => {
                        if (node4.cod3 === item3.cod3 &&
                            node4.cod1 === item3.cod1 &&
                            node4.cod2 === item3.cod2) {
                            itemsFourthLevel.push(node4)
                        }
                    })
                    item3.children = itemsFourthLevel
                })

                item2.children = itemsThirdLevel
            });

            node1.children = itemsSecondLevel;
        });
    }



    private get messagesPanel(): HTMLElement {
        return isObject(this._messagesPanel) ? this._messagesPanel.nativeElement : undefined;
    }

    private manageExecutionProvider = (obj: any) => {
        if (isObject(obj)) {

        }
    }

    private navigateTree(node: any) {

        if (isObject(node) && !isEmpty(get(node, 'cod'))) {
            let code = get(node, 'cod');

            let parts: Array<string> = split(code, '-');

            let actualCode: string;
            if (isArray(parts)) {
                actualCode = head(parts);
                while (actualCode.endsWith('00')) {
                    actualCode = actualCode.substring(0, actualCode.length - 2);
                }
            }

            let existingCod1: string = actualCode.substring(0, 2);
            let existingCod2: string = actualCode.length > 3 ? actualCode.substring(2, 4) : null;
            let existingCod3: string = actualCode.length > 5 ? actualCode.substring(4, 6) : null;
            let existingCod4: string = actualCode.length > 5 ? actualCode.substring(6, 8) : null;
            for (let i = 0; i < this.firstLevel.length; i++) {
                if (this.firstLevel[i].cod1 === existingCod1) {
                    if (existingCod2 != null) {
                        this.firstLevel[i].expanded = true;
                        for (let j = 0; j < this.firstLevel[i].children.length; j++) {
                            if (this.firstLevel[i].children[j].cod2 === existingCod2) {
                                if (existingCod3 != null) {
                                    this.firstLevel[i].children[j].expanded = true;
                                    for (let x = 0; x < this.firstLevel[i].children[j].children.length; x++) {
                                        if (this.firstLevel[i].children[j].children[x].cod3 === existingCod3) {
                                            if (existingCod4 != null) {
                                                this.firstLevel[i].children[j].children[x].expanded = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            set(node, 'styleClass', 'node-highlighted');

        }
    }

    private cleanHighlights() {
        each(this.data, (node) => {
            set(node, 'styleClass', '');
            set(node, 'expanded', false);
        });
    }

    private loadButtons(): void {
        let buttons = {
            "buttons": [
                {
                    "code": "modal-close-button",
                    "label": "BUTTONS.ANNULLA",
                    "icon": "mgg-icons-crud-cancel"
                },
                {
                    "code": "set-button",
                    "label": "BUTTONS.SET",
                    "icon": "mgg-icons-crud-save"
                }
            ]
        }
        this.buttonsSubj = new BehaviorSubject(buttons);
    }

    // #endregion

    // #region Public

    public onOutput($event): void {
        if (isObject($event)) {
            if (has($event, 'op')) {
                return;
            }
            this.actualNode = get($event, 'item');
            let item: TabellatoCpv = get($event, 'item');
            // let cod1: string = item.cod1;
            // let cod2: string = item.cod2;
            // let cod3: string = item.cod3;
            // let cod4: string = item.cod4;
            // let codice = cod1 + (cod2 === '00' ? '' : cod2) + (cod3 === '00' ? '' : cod3) + (cod4 === '00' ? '' : cod4);
            this.calculateDataSubject.next({
                data: item.cod
            });
            this.currentCpv = item.cod;
        }
    }

    public onButtonClick(button: SdkButtonGroupOutput): void {
        if (button.code === 'set-button') {

            this.emitOutput({
                op: 'update',
                value: this.currentCpv,
                item: this.actualNode
            });
        } else {
            this.emitOutput(undefined);
        }
    }

    public onSearchOutput($event) {


        if (!isEmpty($event.data)) {

            this.data = this.originalData;

            this.markForCheck(() => {
                this.sdkHttpLoaderService.showLoader(SdkHttpLoaderType.Operation);
                this.searchResult = [];
                this.cleanHighlights();
                each(this.data, (node) => {
                    let code: string = node.data;
                    let text = node.label;
                    if ($event.data.toLowerCase() === '60112000-6') {
                        if (node.cod.startsWith('6010')) {
                            this.navigateTree(node);
                        }
                    }
                    if (code.toLowerCase().includes($event.data.toLowerCase()) || text.toLowerCase().includes($event.data.toLowerCase())) {

                        this.navigateTree(node);
                    }
                });
                let tempTree = Array<any>();
                each(this.data, (node) => {
                    if (node.expanded == true || node.styleClass == 'node-highlighted') {
                        tempTree.push(node);
                    }
                });
                this.data = tempTree;
                this.populateLevels();
                this.sdkHttpLoaderService.hideLoader();
                this.dataConfig.next({
                    code: 'tree',
                    items: this.firstLevel,
                    selectedValue: this.actualNode
                });
            });
        }
    }

    public onFieldClick(row: any): void {
        this.calculateDataSubject.next({
            data: row.cod
        });
        this.currentCpv = row.cod;
    }

    public cleanResearch() {
        this.data = this.originalData;
        this.cleanHighlights();
        this.populateLevels();


        this.selectedItem = null;
        this.currentCpv = null;
        this.actualNode = null;
        this.dataConfig.next({
            code: 'tree',
            items: this.firstLevel,
            selectedValue: this.actualNode
        });
        this.calculateDataSubject.next({
            data: this.selectedItem
        });
    }

    public manageFormOutput(config: SdkFormBuilderConfiguration): void { }

    // #endregion
}
