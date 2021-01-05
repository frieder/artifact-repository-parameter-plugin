package io.jenkins.plugins.artifactrepo.helper;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import hudson.model.Item;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.artifactrepo.Messages;
import jenkins.model.Jenkins;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import java.util.Collections;

/** A helper class for some tasks repeatedly used across the plugin. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PluginHelper {

  /**
   * Get the Jenkins credentials object of type {@link StandardUsernamePasswordCredentials}
   * identified by the provided ID string.
   *
   * @param credId The id of the Jenkins credential entry.
   * @return Either a credentials object or null.
   */
  public static StandardUsernamePasswordCredentials getCredentials(@Nonnull String credId) {
    Validate.notBlank(credId, Messages.log_missingCreds());

    return CredentialsProvider.lookupCredentials(
            StandardUsernamePasswordCredentials.class, Jenkins.get(), null, Collections.emptyList())
        .stream()
        .filter(cred -> StringUtils.equals(cred.getId(), credId))
        .findFirst()
        .orElse(null);
  }

  /**
   * Returns a model object that can be used to populate credential dropdown widgets. Before
   * returning the credentials it checks the permissions of the requesting user.
   *
   * @param item Needed to check the context of the request.
   * @param credId The id of the Jenkins credentials entry.
   * @return A ListBoxModel with all username/password credentials.
   */
  public static ListBoxModel getCredentialsDropdown(Item item, String credId) {
    Validate.notNull(item, Messages.log_missingItem());

    StandardListBoxModel result = new StandardListBoxModel();

    if (item == null && !Jenkins.get().hasPermission(Jenkins.ADMINISTER)
        || item != null
            && (!item.hasPermission(Item.EXTENDED_READ)
                || !item.hasPermission(CredentialsProvider.USE_ITEM))) {
      return result.includeCurrentValue(credId);
    }

    return result
        .includeEmptyValue()
        .includeMatchingAs(
            ACL.SYSTEM,
            item,
            StandardUsernameCredentials.class,
            Collections.emptyList(),
            CredentialsMatchers.always())
        .includeCurrentValue(credId);
  }
}
