package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Handles network packet registration for Alex's Mobs.
 * Register this in the main mod class with: modEventBus.addListener(AMNetworking::register);
 */
public class AMNetworking {

    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        // Server -> Client packets
        registrar.playToClient(
            MessageSyncEntityPos.ID,
            MessageSyncEntityPos.CODEC,
            MessageSyncEntityPos::handleClient
        );
        registrar.playToClient(
            MessageStartDancing.ID,
            MessageStartDancing.CODEC,
            MessageStartDancing::handleClient
        );
        registrar.playToClient(
            MessageTarantulaHawkSting.ID,
            MessageTarantulaHawkSting.CODEC,
            MessageTarantulaHawkSting::handleClient
        );
        registrar.playToClient(
            MessageMosquitoMountPlayer.ID,
            MessageMosquitoMountPlayer.CODEC,
            MessageMosquitoMountPlayer::handleClient
        );
        // Bidirectional - used by both server->client and client->server
        registrar.playBidirectional(
            MessageMosquitoDismount.ID,
            MessageMosquitoDismount.CODEC,
            MessageMosquitoDismount::handle
        );
        registrar.playToClient(
            MessageCrowMountPlayer.ID,
            MessageCrowMountPlayer.CODEC,
            MessageCrowMountPlayer::handleClient
        );
        registrar.playToClient(
            MessageCrowDismount.ID,
            MessageCrowDismount.CODEC,
            MessageCrowDismount::handleClient
        );
        registrar.playToClient(
            MessageMungusBiomeChange.ID,
            MessageMungusBiomeChange.CODEC,
            MessageMungusBiomeChange::handleClient
        );
        registrar.playToClient(
            MessageUpdateCapsid.ID,
            MessageUpdateCapsid.CODEC,
            MessageUpdateCapsid::handleClient
        );
        registrar.playToClient(
            MessageKangarooInventorySync.ID,
            MessageKangarooInventorySync.CODEC,
            MessageKangarooInventorySync::handleClient
        );
        registrar.playToClient(
            MessageKangarooEat.ID,
            MessageKangarooEat.CODEC,
            MessageKangarooEat::handleClient
        );
        registrar.playToClient(
            MessageSendVisualFlagFromServer.ID,
            MessageSendVisualFlagFromServer.CODEC,
            MessageSendVisualFlagFromServer::handleClient
        );
        registrar.playToClient(
            MessageSetPupfishChunkOnClient.ID,
            MessageSetPupfishChunkOnClient.CODEC,
            MessageSetPupfishChunkOnClient::handleClient
        );
        registrar.playToClient(
            MessageUpdateTransmutablesToDisplay.ID,
            MessageUpdateTransmutablesToDisplay.CODEC,
            MessageUpdateTransmutablesToDisplay::handleClient
        );
        registrar.playToClient(
            MessageSyncEntityData.ID,
            MessageSyncEntityData.CODEC,
            MessageSyncEntityData::handleClient
        );

        // Client -> Server packets
        registrar.playToServer(
            MessageSwingArm.ID,
            MessageSwingArm.CODEC,
            MessageSwingArm::handleServer
        );
        registrar.playToServer(
            MessageUpdateEagleControls.ID,
            MessageUpdateEagleControls.CODEC,
            MessageUpdateEagleControls::handleServer
        );
        // Bidirectional - multipart packets need to go both ways
        registrar.playBidirectional(
            MessageHurtMultipart.ID,
            MessageHurtMultipart.CODEC,
            MessageHurtMultipart::handle
        );
        registrar.playBidirectional(
            MessageInteractMultipart.ID,
            MessageInteractMultipart.CODEC,
            MessageInteractMultipart::handle
        );
        registrar.playToServer(
            MessageTransmuteFromMenu.ID,
            MessageTransmuteFromMenu.CODEC,
            MessageTransmuteFromMenu::handleServer
        );
    }
}
