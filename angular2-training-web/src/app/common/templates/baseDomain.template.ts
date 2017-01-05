import {DomainMetadata, FormMetadata} from "./form-metadata";
import {BaseEntity} from "../entities/baseEntity";

export abstract class BaseDomainTemplate<E extends BaseEntity> {

  metadataArray: DomainMetadata[];

  constructor() {
    this.metadataArray = this.initMetadataArray();
  }

  mapDomainFormMetadata(entity: E): FormMetadata<any>[] {
    return FormMetadata.getFormMetadataArray(this.metadataArray, entity);
  }

  protected abstract initMetadataArray(): DomainMetadata[];

  abstract getDetailUrl():string;

  abstract getTableUrl():string;

  abstract getPropertiesLables():string;

  abstract getTableTitle(): string;

  abstract getDetailTitle(): string;
}
