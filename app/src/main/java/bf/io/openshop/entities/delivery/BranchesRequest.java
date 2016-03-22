package bf.io.openshop.entities.delivery;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BranchesRequest {

    @SerializedName("records")
    private List<Branch> branches;

    public BranchesRequest(List<Branch> branches) {
        this.branches = branches;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BranchesRequest that = (BranchesRequest) o;

        return !(branches != null ? !branches.equals(that.branches) : that.branches != null);

    }

    @Override
    public int hashCode() {
        return branches != null ? branches.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "BranchesRequest{" +
                "branches=" + branches +
                '}';
    }
}
