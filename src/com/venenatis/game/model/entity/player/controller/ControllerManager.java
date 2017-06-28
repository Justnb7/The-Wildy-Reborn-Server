package com.venenatis.game.model.entity.player.controller;

import com.venenatis.game.model.entity.player.controller.impl.DefaultController;
import com.venenatis.game.model.entity.player.controller.impl.ZeroInteractionController;

/**
 * Handles controllers for specific areas to moderate a players gameplay
 * 
 * @author Mobster
 * @author Mikey`
 *
 */
public class ControllerManager {

    /**
     * The default controller
     */
    public static final DefaultController DEFAULT_CONTROLLER = new DefaultController();
    
    /**
     * The no zero interaction controller
     */
    public static final ZeroInteractionController ZERO_INTERACTION_CONTROLLER = new ZeroInteractionController();
    
}