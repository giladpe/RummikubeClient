/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rummikub.view;

import rummikub.client.ws.RummikubWebServiceService;

/**
 *
 * @author giladPe
 */
public interface ServerConnection {
    public void setService(RummikubWebServiceService service);
    public void setPlayerId(int playerId);
    public int getPlayerId();
    public RummikubWebServiceService getService();
}
