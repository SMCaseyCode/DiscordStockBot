package SMCaseyCode;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.historical.trade.Trade;
import net.jacobpeterson.alpaca.model.properties.DataAPIType;
import net.jacobpeterson.alpaca.model.properties.EndpointAPIType;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;

import static SMCaseyCode.ProtectedData.KEYID;
import static SMCaseyCode.ProtectedData.SECRETKEY;

public class AlpacaManager {

    AlpacaAPI alpacaAPI = new AlpacaAPI(KEYID.getContent(), SECRETKEY.getContent(), EndpointAPIType.PAPER, DataAPIType.IEX);

    public Trade alpacaGetTrade(String stockSymbol){

        try {
            return alpacaAPI.marketData().getLatestTrade(stockSymbol).getTrade();
        } catch (AlpacaClientException e){
            System.out.println("ALPACA EXCEPTION: " + e);
        }

        return null;
    }

}
