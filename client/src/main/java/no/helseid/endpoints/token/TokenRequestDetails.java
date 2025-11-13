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
  private final Set<String> scope;

  /**
   * Create a new instance of TokenRequestDetails
   * It is recomended to use the Builder class
   *
   * @param tenancy the client tenancy
   * @param parentOrganizationNumber the parent organization number if relevant
   * @param childOrganizationNumber the child organization number if relevant
   * @param sfmJournalId the SFM journal id if relevant
   * @param scope the scopes for the current request if relevant
   */
  public TokenRequestDetails(
      Tenancy tenancy,
      String parentOrganizationNumber,
      String childOrganizationNumber,
      String sfmJournalId,
      Set<String> scope
  ) {
    this.tenancy = tenancy;
    this.parentOrganizationNumber = parentOrganizationNumber;
    this.childOrganizationNumber = childOrganizationNumber;
    this.sfmJournalId = sfmJournalId;
    this.scope = scope;
  }

  /**
   * Returning the tenanct of the request
   * @return the tenancy of the request
   */
  public Tenancy tenancy() {
    return tenancy;
  }

  /**
   * Returning the parent organization number of the request
   * @return the parent organization number of the request
   */
  public String parentOrganizationNumber() {
    return parentOrganizationNumber;
  }

  /**
   * Returning the child organization number of the request
   * @return the child organization number of the request
   */
  public String childOrganizationNumber() {
    return childOrganizationNumber;
  }

  /**
   * Returning the SFM journal-id of the request
   * @return the SFM journal-id of the request
   */
  public String sfmJournalId() {
    return sfmJournalId;
  }

  /**
   * Returning the scope set of the request
   * @return the scope set of the request
   */
  public Set<String> scope() {
    return scope;
  }

  /**
   * Builder class for TokenRequestDetails
   */
  public static class Builder {
    private final Set<String> scopeSet = new HashSet<>();
    private Tenancy tenancy = Tenancy.SINGLE_TENANT;
    private String parentOrganizationNumber;
    private String childOrganizationNumber;
    private String sfmJournalId;

    /**
     * Create a new Builder instance for TokenRequestDetails
     */
    public Builder() {
    }

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
     * @return the current builder
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
      this.scopeSet.add(scope);
      return this;
    }

    /**
     * Add multiple scopes to the token request details
     *
     * @param scopes the set of scopes to be included in the request
     * @return the current builder
     */
    public Builder addMultipleScope(Set<String> scopes) {
      this.scopeSet.addAll(scopes);
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
          Collections.unmodifiableSet(scopeSet)
      );
    }
  }
}
