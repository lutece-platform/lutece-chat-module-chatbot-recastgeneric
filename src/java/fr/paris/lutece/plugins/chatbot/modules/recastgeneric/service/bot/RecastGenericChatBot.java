/*
 * Copyright (c) 2002-2017, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.chatbot.modules.recastgeneric.service.bot;

import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.plugins.chatbot.service.bot.AbstractChatBot;
import fr.paris.lutece.plugins.chatbot.service.bot.ChatBot;
import fr.paris.lutece.plugins.recast.business.DialogResponse;
import fr.paris.lutece.plugins.recast.business.Message;
import fr.paris.lutece.plugins.recast.service.RecastDialogService;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.l10n.LocaleService;
import fr.paris.lutece.util.httpaccess.HttpAccessException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * RecastGenericChatBot
 */
public class RecastGenericChatBot extends AbstractChatBot implements ChatBot
{

    private static final String DSKEY_BOT_NAME = "module.chatbot.recastgeneric.site_property.bot_name";
    private static final String DSKEY_BOT_DESCRIPTION = "module.chatbot.recastgeneric.site_property.bot_description";
    private static final String DSKEY_TOKEN = "module.chatbot.recastgeneric.site_property.bot_token";
    private static final String NOT_FOUND = "";
    private static final String MESSAGE_TO_BE_DEFINED = "module.chatbot.recastgeneric.message.tokenMustBeDefined";
    private static final String NOT_DEFINED = I18nService.getLocalizedString( MESSAGE_TO_BE_DEFINED, LocaleService.getDefault() );
    private static final String MESSAGE_INVALID_CONFIG = "module.chatbot.recastgeneric.message.invalidConfig";
    private static final String MSG_INVALID_CONFIG = I18nService.getLocalizedString( MESSAGE_INVALID_CONFIG, LocaleService.getDefault() );

    /**
     * {@inheritDoc }
     */
    @Override
    public List<String> processUserMessage( String strMessage, String strConversationId, Locale locale )
    {
        List<String> listMessages = new ArrayList<>();
        DialogResponse response = null;
        if( !DatastoreService.existsKey( DSKEY_TOKEN ) )
        {

        }
        String strToken = getToken();
        if( strToken.equals( NOT_DEFINED ) )
        {
            listMessages.add( MSG_INVALID_CONFIG );
            return listMessages;
        }
        
        try
        {
            response = RecastDialogService.getDialogResponse( strMessage, strConversationId, strToken, locale.getLanguage() );
        }
        catch( IOException | HttpAccessException ex )
        {
            AppLogService.error( "Error accessing recast API : " + ex.getMessage(), ex );
        }

        if( response != null )
        {
            for( Message message : response.getMessages() )
            {
                listMessages.add( message.getContent() );
            }
        }
        return listMessages;
    }
    

    /**
     * {@inheritDoc }
     */
    @Override
    public String getName( Locale locale )
    {
        return DatastoreService.getDataValue( DSKEY_BOT_NAME, NOT_FOUND );
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public String getDescription( Locale locale )
    {
        return DatastoreService.getDataValue( DSKEY_BOT_DESCRIPTION, NOT_FOUND );
    }
    
    /**
     * retrieve the bot token in the datastore
     * @return The bot token
     */
    private String getToken()
    {
        if( !DatastoreService.existsKey( DSKEY_TOKEN ) )
        {
            DatastoreService.setDataValue( DSKEY_BOT_NAME, NOT_DEFINED );
            DatastoreService.setDataValue( DSKEY_BOT_DESCRIPTION, NOT_DEFINED );
            DatastoreService.setDataValue( DSKEY_TOKEN, NOT_DEFINED );
        }
        return DatastoreService.getDataValue( DSKEY_TOKEN, NOT_FOUND );
    }

}
