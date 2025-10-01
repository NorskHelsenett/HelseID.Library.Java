package no.helseid.clientassertion;

import no.helseid.configuration.Tenancy;
import no.helseid.endpoints.token.TokenRequestDetails;
import no.helseid.exceptions.HelseIdException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for creating the assertion details
 */
public abstract class AssertionDetails {
  public static Object fromTokenRequestDetails(TokenRequestDetails details) throws HelseIdException {
    List<Map<String, Object>> assertionDetails = new ArrayList<>();

    if (details.tenancy() == Tenancy.SINGLE_TENANT && details.childOrganizationNumber() != null) {
      assertionDetails.add(
          HelseIdAuthorizationClaimUtil.createClaimHelseidAuthorizationForSingleTenant(details.childOrganizationNumber())
      );
    } else if (details.tenancy() == Tenancy.MULTI_TENANT && details.parentOrganizationNumber() != null) {
      assertionDetails.add(
          HelseIdAuthorizationClaimUtil.createClaimHelseidAuthorizationForMultiTenant(details.parentOrganizationNumber(), details.childOrganizationNumber())
      );
    }

    if (details.sfmJournalId() != null) {
      assertionDetails.add(
          SFMJournalIdClaimUtil.createClaimSFMJournalId(details.sfmJournalId())
      );
    }

    if (assertionDetails.isEmpty()) {
      throw new HelseIdException("The resulting assertion details is empty");
    }

    if (assertionDetails.size() == 1) {
      return assertionDetails.get(0);
    }

    return assertionDetails;
  }
}
