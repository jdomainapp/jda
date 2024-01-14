import { PatternConsumer } from "src/app/modules/base/pattern/pattern.consumer";

export class AutoSearchConsumer extends PatternConsumer {
    
    override onAction(action: string, event: any): void {
        this.host.searchKeyword = event;
    }
    
}