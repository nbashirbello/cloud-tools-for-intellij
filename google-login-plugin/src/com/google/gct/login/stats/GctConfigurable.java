package com.google.gct.login.stats;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * Implementation of {@code applicationConfigurable} extension that provides a
 * Google Cloud Tools tab in the "Settings" dialog.
 */
public class GctConfigurable implements SearchableConfigurable {
    private JCheckBox enableUsageTrackerBox;
    private static final Logger LOG = Logger.getInstance(GctConfigurable.class);
    private static final String ENABLE_TRACKER_TEXT =
            "Would you like to help Google improve the Cloud Tools for IntelliJ plug-in?";
    private static final String PRIVACY_POLICY_URL = "http://www.google.com/policies/privacy/";
    private static final String PRIVACY_POLICY_TEXT = "<html>Your use of this plugin is subject to "
            + "the Apache 2.0 software license and the <a href=\"" + PRIVACY_POLICY_URL
            + "\">Google Privacy Policy</a>.</html>";


    public GctConfigurable() {
    }

    @NotNull
    @Override
    public String getId() {
        return "google.usage.tracker";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Google Cloud Tools Plugin";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Add the Usage Tracker Box
        JPanel usageTrackerGroup  = creatUsageTrackerComponent();
        mainPanel.add(usageTrackerGroup, BorderLayout.NORTH);

        return mainPanel;
    }

    @Override
    public boolean isModified() {
        return UsageTrackerManager.getOptIn() != enableUsageTrackerBox.isSelected();
    }

    @Override
    public void apply() throws ConfigurationException {
        UsageTrackerManager.setOptIn(enableUsageTrackerBox.isSelected());
    }

    @Override
    public void reset() {
        enableUsageTrackerBox.setSelected(UsageTrackerManager.getOptIn());
    }

    @Override
    public void disposeUIResources() {
        enableUsageTrackerBox = null;
    }

    private JPanel creatUsageTrackerComponent() {
        enableUsageTrackerBox = new JCheckBox(ENABLE_TRACKER_TEXT);
        enableUsageTrackerBox.setSelected(UsageTrackerManager.getOptIn());

        // Disable checkbox if usage tracker property has not been configured
        if (!UsageTrackerManager.isTrackingConfigured()) {
            enableUsageTrackerBox.setEnabled(false);
        }

        final JLabel privacyPolicyText = new JLabel(PRIVACY_POLICY_TEXT);
        privacyPolicyText.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent me) {
                privacyPolicyText.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent me) {
                privacyPolicyText.setCursor(Cursor.getDefaultCursor());
            }
            public void mouseClicked(MouseEvent me)
            {
                try {
                    BrowserUtil.browse(new URL(PRIVACY_POLICY_URL));
                }
                catch(Exception e) {
                    GctConfigurable.LOG.error(e);
                }
            }
        });

        JPanel usageTrackerGroup = new JPanel(new BorderLayout());
        usageTrackerGroup.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Usage Tracker"));
        usageTrackerGroup.add(enableUsageTrackerBox, BorderLayout.NORTH);
        usageTrackerGroup.add(privacyPolicyText, BorderLayout.SOUTH);
        return usageTrackerGroup;
    }
}
