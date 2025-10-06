package no.helseid.endpoints.token;

import no.helseid.configuration.Tenancy;
import no.helseid.exceptions.HelseIdException;

import java.util.*;

/**
 * A collection of details in a token request
 */
public final class TokenRequestDetails {
  private final Tenancy tenancy;
  private final String parentOrganizationNumber;
  private final String childOrganizationNumber;
  private final String sfmJournalId;
  private final String scope;

  public TokenRequestDetails(
      Tenancy tenancy,
      String parentOrganizationNumber,
      String childOrganizationNumber,
      String sfmJournalId,
      String scope
  ) {
    this.tenancy = tenancy;
    this.parentOrganizationNumber = parentOrganizationNumber;
    this.childOrganizationNumber = childOrganizationNumber;
    this.sfmJournalId = sfmJournalId;
    this.scope = scope;
  }

  public Tenancy tenancy() {
    return tenancy;
  }

  public String parentOrganizationNumber() {
    return parentOrganizationNumber;
  }

  public String childOrganizationNumber() {
    return childOrganizationNumber;
  }

  public String sfmJournalId() {
    return sfmJournalId;
  }

  public String scope() {
    return scope;
  }

  public static class Builder {
    private final List<String> scopeList = new ArrayList<>();
    private Tenancy tenancy = Tenancy.SINGLE_TENANT;
    private String parentOrganizationNumber;
    private String childOrganizationNumber;
    private String sfmJournalId;

    /**
     * Assign a tenancy for the client, default to single tenancy
     *
     * @param tenancy the client tenancy
     * @return the current builder
     */
    public Builder withTenancy(Tenancy tenancy) {
      this.tenancy = tenancy;
      return this;
    }

    /**
     * Assign an organization number representing the parent organization
     *
     * @param parentOrganizationNumber identifies the parent organization, only relevant for multi tenant clients
     */
    public Builder withParentOrganizationNumber(String parentOrganizationNumber) {
      this.parentOrganizationNumber = parentOrganizationNumber;
      return this;
    }

    /**
     * Assign an organization number representing the child organization
     *
     * @param childOrganizationNumber identifies the child organization
     * @return the current builder
     */
    public Builder withChildOrganizationNumber(String childOrganizationNumber) {
      this.childOrganizationNumber = childOrganizationNumber;
      return this;
    }

    /**
     * Assign a SFM-journal id representing the specific SFM journal
     *
     * @param sfmJournalId identifies the SFM journal, only relevant in relation to use of Sentral Forskrivningsmodul
     * @return the current builder
     */
    public Builder withSfmJournalId(String sfmJournalId) {
      this.sfmJournalId = sfmJournalId;
      return this;
    }

    /**
     * Add a scope to the token request details
     *
     * @param scope the scope to be included in the request
     * @return the current builder
     */
    public Builder addScope(String scope) {
      this.scopeList.add(scope);
      return this;
    }

    /**
     * Add multiple scopes to the token request details
     *
     * @param scopeList the list of scopes to be included in the request
     * @return the current builder
     */
    public Builder addMultipleScope(List<String> scopeList) {
      this.scopeList.addAll(scopeList);
      return this;
    }

    /**
     * Process the provided values into an TokenRequestDetails object
     *
     * @return details used in a token request
     * @throws HelseIdException when the builder is misconfigured
     */
    public TokenRequestDetails build() throws HelseIdException {
      return new TokenRequestDetails(
          tenancy,
          parentOrganizationNumber,
          childOrganizationNumber,
          sfmJournalId,
          String.join(" ", scopeList)
      );
    }
  }
}
