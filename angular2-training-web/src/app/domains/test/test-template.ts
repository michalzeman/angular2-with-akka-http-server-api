import {BaseDomainTemplate} from "../../common/templates/baseDomain.template";
import {Test} from "./test";
import {DomainMetadata, FormMetadata} from "../../common/templates/form-metadata";
import {Injectable} from "@angular/core";

@Injectable()
export class TestTemplate extends BaseDomainTemplate<Test> {

  constructor() {
    super();
  }

  protected initMetadataArray(): DomainMetadata[] {
    return [new DomainMetadata({key: 'id', controlType: 'textbox', table: true, label: 'ID', required: true})];
  }

  mapDomainFormMetadata(entity:Test):FormMetadata<any>[] {
    let item = new FormMetadata<number>(
      new DomainMetadata({key: 'id', controlType: 'textbox', table: false, label: 'Id', required: true}), entity);
    return [item];
  }

  getDetailUrl(): string {
    return '/test';
  }

  getTableUrl(): string {
    return 'tests';
  }

  getPropertiesLables(): string {
    return '';
  }

  getTableTitle(): string {
    return 'Tests';
  }

}
