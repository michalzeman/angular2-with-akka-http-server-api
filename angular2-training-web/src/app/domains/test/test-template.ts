import {BaseDomainTemplate} from "../../common/templates/baseDomain.template";
import {Test} from "./test";
import {DomainMetadata} from "../../common/templates/domain-metadata";
import {Injectable} from "@angular/core";

@Injectable()
export class TestTemplate extends BaseDomainTemplate<Test> {

  constructor() {
    super();
  }

  protected initMetadataArray(): DomainMetadata[] {
    return [new DomainMetadata({key: 'id', controlType: 'textbox', table: true, label: 'ID', required: true})];
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
