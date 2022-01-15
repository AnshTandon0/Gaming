package com.gaming.community.flexster.menu;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gaming.community.flexster.R;
import com.google.android.material.tabs.TabLayout;

public class PrivacyActivity extends AppCompatActivity {

    ImageView imageView;
    LinearLayout ll_privacy_policy,ll_terms_conditions;
    TabLayout tabLayout;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        imageView = findViewById(R.id.imageView);
        tabLayout = findViewById(R.id.tabLayout);
        ll_privacy_policy = findViewById(R.id.ll_privacy_policy);
        ll_terms_conditions = findViewById(R.id.ll_terms_conditions);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0) {

                    ll_terms_conditions.setVisibility(View.GONE);
                    ll_privacy_policy.setVisibility(View.VISIBLE);

                }
                else if (tabLayout.getSelectedTabPosition() == 1) {

                    ll_privacy_policy.setVisibility(View.GONE);
                    ll_terms_conditions.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        /*WebView webView = findViewById(R.id.webView);
        findViewById(R.id.imageView).setOnClickListener(v -> onBackPressed());
        webView.requestFocus();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.setSoundEffectsEnabled(true);
        webView.loadData("<!DOCTYPE html>\n" +
                        "    <html>\n" +
                        "    <head>\n" +
                        "      <meta charset='utf-8'>\n" +
                        "      <meta name='viewport' content='width=device-width'>\n" +
                        "      <title>Privacy Policy</title>\n" +
                        "      <style> body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; padding:1em; } </style>\n" +
                        "    </head>\n" +
                        "    <body>\n" +
                        "    <strong>Privacy Policy</strong> <p>\n" +
                        "                  Spacester built the Myfriend app as\n" +
                        "                  a Free app. This SERVICE is provided by\n" +
                        "                  Spacester at no cost and is intended for use as\n" +
                        "                  is.\n" +
                        "                </p> <p>\n" +
                        "                  This page is used to inform visitors regarding our\n" +
                        "                  policies with the collection, use, and disclosure of Personal\n" +
                        "                  Information if anyone decided to use our Service.\n" +
                        "                </p> <p>\n" +
                        "                  If you choose to use our Service, then you agree to\n" +
                        "                  the collection and use of information in relation to this\n" +
                        "                  policy. The Personal Information that we collect is\n" +
                        "                  used for providing and improving the Service. We will not use or share your information with\n" +
                        "                  anyone except as described in this Privacy Policy.\n" +
                        "                </p> <p>\n" +
                        "                  The terms used in this Privacy Policy have the same meanings\n" +
                        "                  as in our Terms and Conditions, which is accessible at\n" +
                        "                  Myfriend unless otherwise defined in this Privacy Policy.\n" +
                        "                </p> <p><strong>Information Collection and Use</strong></p> <p>\n" +
                        "                  For a better experience, while using our Service, we\n" +
                        "                  may require you to provide us with certain personally\n" +
                        "                  identifiable information, including but not limited to spacester@gmail.com. The information that\n" +
                        "                  we request will be retained by us and used as described in this privacy policy.\n" +
                        "                </p> <div><p>\n" +
                        "                    The app does use third party services that may collect\n" +
                        "                    information used to identify you.\n" +
                        "                  </p> <p>\n" +
                        "                    Link to privacy policy of third party service providers used\n" +
                        "                    by the app\n" +
                        "                  </p> <ul><li><a href=\"https://www.google.com/policies/privacy/\" target=\"_blank\" rel=\"noopener noreferrer\">Google Play Services</a></li><li><a href=\"https://support.google.com/admob/answer/6128543?hl=en\" target=\"_blank\" rel=\"noopener noreferrer\">AdMob</a></li><li><a href=\"https://firebase.google.com/policies/analytics\" target=\"_blank\" rel=\"noopener noreferrer\">Google Analytics for Firebase</a></li><li><a href=\"https://firebase.google.com/support/privacy/\" target=\"_blank\" rel=\"noopener noreferrer\">Firebase Crashlytics</a></li><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><li><a href=\"https://www.mapbox.com/legal/privacy\" target=\"_blank\" rel=\"noopener noreferrer\">Mapbox</a></li><!----><!----><!----></ul></div> <p><strong>Log Data</strong></p> <p>\n" +
                        "                  We want to inform you that whenever you\n" +
                        "                  use our Service, in a case of an error in the app\n" +
                        "                  we collect data and information (through third party\n" +
                        "                  products) on your phone called Log Data. This Log Data may\n" +
                        "                  include information such as your device Internet Protocol\n" +
                        "                  (“IP”) address, device name, operating system version, the\n" +
                        "                  configuration of the app when utilizing our Service,\n" +
                        "                  the time and date of your use of the Service, and other\n" +
                        "                  statistics.\n" +
                        "                </p> <p><strong>Cookies</strong></p> <p>\n" +
                        "                  Cookies are files with a small amount of data that are\n" +
                        "                  commonly used as anonymous unique identifiers. These are sent\n" +
                        "                  to your browser from the websites that you visit and are\n" +
                        "                  stored on your device's internal memory.\n" +
                        "                </p> <p>\n" +
                        "                  This Service does not use these “cookies” explicitly. However,\n" +
                        "                  the app may use third party code and libraries that use\n" +
                        "                  “cookies” to collect information and improve their services.\n" +
                        "                  You have the option to either accept or refuse these cookies\n" +
                        "                  and know when a cookie is being sent to your device. If you\n" +
                        "                  choose to refuse our cookies, you may not be able to use some\n" +
                        "                  portions of this Service.\n" +
                        "                </p> <p><strong>Service Providers</strong></p> <p>\n" +
                        "                  We may employ third-party companies and\n" +
                        "                  individuals due to the following reasons:\n" +
                        "                </p> <ul><li>To facilitate our Service;</li> <li>To provide the Service on our behalf;</li> <li>To perform Service-related services; or</li> <li>To assist us in analyzing how our Service is used.</li></ul> <p>\n" +
                        "                  We want to inform users of this Service\n" +
                        "                  that these third parties have access to your Personal\n" +
                        "                  Information. The reason is to perform the tasks assigned to\n" +
                        "                  them on our behalf. However, they are obligated not to\n" +
                        "                  disclose or use the information for any other purpose.\n" +
                        "                </p> <p><strong>Security</strong></p> <p>\n" +
                        "                  We value your trust in providing us your\n" +
                        "                  Personal Information, thus we are striving to use commercially\n" +
                        "                  acceptable means of protecting it. But remember that no method\n" +
                        "                  of transmission over the internet, or method of electronic\n" +
                        "                  storage is 100% secure and reliable, and we cannot\n" +
                        "                  guarantee its absolute security.\n" +
                        "                </p> <p><strong>Links to Other Sites</strong></p> <p>\n" +
                        "                  This Service may contain links to other sites. If you click on\n" +
                        "                  a third-party link, you will be directed to that site. Note\n" +
                        "                  that these external sites are not operated by us.\n" +
                        "                  Therefore, we strongly advise you to review the\n" +
                        "                  Privacy Policy of these websites. We have\n" +
                        "                  no control over and assume no responsibility for the content,\n" +
                        "                  privacy policies, or practices of any third-party sites or\n" +
                        "                  services.\n" +
                        "                </p> <p><strong>Children’s Privacy</strong></p> <p>\n" +
                        "                  These Services do not address anyone under the age of 13.\n" +
                        "                  We do not knowingly collect personally\n" +
                        "                  identifiable information from children under 13 years of age. In the case\n" +
                        "                  we discover that a child under 13 has provided\n" +
                        "                  us with personal information, we immediately\n" +
                        "                  delete this from our servers. If you are a parent or guardian\n" +
                        "                  and you are aware that your child has provided us with\n" +
                        "                  personal information, please contact us so that\n" +
                        "                  we will be able to do necessary actions.\n" +
                        "                </p> <p><strong>Changes to This Privacy Policy</strong></p> <p>\n" +
                        "                  We may update our Privacy Policy from\n" +
                        "                  time to time. Thus, you are advised to review this page\n" +
                        "                  periodically for any changes. We will\n" +
                        "                  notify you of any changes by posting the new Privacy Policy on\n" +
                        "                  this page.\n" +
                        "                </p> <p>This policy is effective as of 2021-07-03</p> <p><strong>Contact Us</strong></p> <p>\n" +
                        "                  If you have any questions or suggestions about our\n" +
                        "                  Privacy Policy, do not hesitate to contact us at email.\n" +
                        "                </p>\n" +
                        "    </body>\n" +
                        "    </html>",
                "text/html", "UTF-8");*/

    }
}