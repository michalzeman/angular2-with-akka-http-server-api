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
    return [
      new DomainMetadata({key: 'id', controlType: 'textbox', table: true, label: 'ID', required: true}),
      new DomainMetadata({key:'name', controlType:'textbox', table:true, label:'Name', required:true}),
      new DomainMetadata({key:'itemId', controlType:'dropdown', table:true, label:'Item', required:true}),
    ];
  }

  getDetailTitle(): string {
    return 'Test';
  }

  getDetailUrl(): string {
    return '/test';
  }

  getTableUrl(): string {
    return '/tests';
  }

  getPropertiesLables(): string {
    return '';
  }

  getTableTitle(): string {
    return 'Tests';
  }

}
