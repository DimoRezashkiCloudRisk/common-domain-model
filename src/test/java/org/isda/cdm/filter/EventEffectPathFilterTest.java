package org.isda.cdm.filter;

import com.regnosys.rosetta.common.inspection.PathObject;
import com.regnosys.rosetta.common.inspection.PathTypeNode;
import com.regnosys.rosetta.common.inspection.RosettaNodeInspector;
import com.regnosys.rosetta.common.util.HierarchicalPath;
import org.isda.cdm.*;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.regnosys.rosetta.common.inspection.RosettaNodeInspector.Visitor;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

class EventEffectPathFilterTest {

    @Test
    void shouldFilterPathsForEffectedContract() {
        HierarchicalPath effectedContractPath = HierarchicalPath.valueOf("eventEffect.effectedContract");

        assertThat(EventEffectPathFilter.test(effectedContractPath, Contract.class, HierarchicalPath.valueOf("primitive.newTrade.contract")), is(false));
        assertThat(EventEffectPathFilter.test(effectedContractPath, Contract.class, HierarchicalPath.valueOf("primitive.exercise.before.contract")), is(true));
        assertThat(EventEffectPathFilter.test(effectedContractPath, Contract.class, HierarchicalPath.valueOf("primitive.exercise.after.contract")), is(false));
        assertThat(EventEffectPathFilter.test(effectedContractPath, Contract.class, HierarchicalPath.valueOf("primitive.termsChange.before.contract")), is(true));
        assertThat(EventEffectPathFilter.test(effectedContractPath, Contract.class, HierarchicalPath.valueOf("primitive.termsChange.after.contract")), is(false));
        assertThat(EventEffectPathFilter.test(effectedContractPath, Contract.class, HierarchicalPath.valueOf("primitive.quantityChange.before.contract")), is(true));
        assertThat(EventEffectPathFilter.test(effectedContractPath, Contract.class, HierarchicalPath.valueOf("primitive.quantityChange.after.contract")), is(false));
    }

    @Test
    void shouldFilterPathsForEffectedContractReference() {
        HierarchicalPath effectedContractReferencePath = HierarchicalPath.valueOf("eventEffect.effectedContractReference");

        assertThat(EventEffectPathFilter.test(effectedContractReferencePath, ContractReference.class, HierarchicalPath.valueOf("primitive.exercise.before.contractReference")), is(true));
        assertThat(EventEffectPathFilter.test(effectedContractReferencePath, ContractReference.class, HierarchicalPath.valueOf("primitive.exercise.after.contractReference")), is(false));
        assertThat(EventEffectPathFilter.test(effectedContractReferencePath, ContractReference.class, HierarchicalPath.valueOf("primitive.termsChange.before.contractReference")), is(true));
        assertThat(EventEffectPathFilter.test(effectedContractReferencePath, ContractReference.class, HierarchicalPath.valueOf("primitive.termsChange.after.contractReference")), is(false));
        assertThat(EventEffectPathFilter.test(effectedContractReferencePath, ContractReference.class, HierarchicalPath.valueOf("primitive.quantityChange.before.contractReference")), is(true));
        assertThat(EventEffectPathFilter.test(effectedContractReferencePath, ContractReference.class, HierarchicalPath.valueOf("primitive.quantityChange.after.contractReference")), is(false));
    }

    @Test
    void shouldFilterPathsForContract() {
        HierarchicalPath contractPath = HierarchicalPath.valueOf("eventEffect.contract");

        assertThat(EventEffectPathFilter.test(contractPath, Contract.class, HierarchicalPath.valueOf("primitive.newTrade.contract")), is(true));
        assertThat(EventEffectPathFilter.test(contractPath, Contract.class, HierarchicalPath.valueOf("primitive.exercise.before.contract")), is(false));
        assertThat(EventEffectPathFilter.test(contractPath, Contract.class, HierarchicalPath.valueOf("primitive.exercise.after.contract")), is(true));
        assertThat(EventEffectPathFilter.test(contractPath, Contract.class, HierarchicalPath.valueOf("primitive.termsChange.before.contract")), is(false));
        assertThat(EventEffectPathFilter.test(contractPath, Contract.class, HierarchicalPath.valueOf("primitive.termsChange.after.contract")), is(true));
        assertThat(EventEffectPathFilter.test(contractPath, Contract.class, HierarchicalPath.valueOf("primitive.quantityChange.before.contract")), is(false));
        assertThat(EventEffectPathFilter.test(contractPath, Contract.class, HierarchicalPath.valueOf("primitive.quantityChange.after.contract")), is(true));
    }

    @Test
    void shouldFilterPathsForContractReference() {
        HierarchicalPath contractReferencePath = HierarchicalPath.valueOf("eventEffect.contractReference");

        assertThat(EventEffectPathFilter.test(contractReferencePath, ContractReference.class, HierarchicalPath.valueOf("primitive.exercise.before.contractReference")), is(false));
        assertThat(EventEffectPathFilter.test(contractReferencePath, ContractReference.class, HierarchicalPath.valueOf("primitive.exercise.after.contractReference")), is(true));
        assertThat(EventEffectPathFilter.test(contractReferencePath, ContractReference.class, HierarchicalPath.valueOf("primitive.termsChange.before.contractReference")), is(false));
        assertThat(EventEffectPathFilter.test(contractReferencePath, ContractReference.class, HierarchicalPath.valueOf("primitive.termsChange.after.contractReference")), is(true));
        assertThat(EventEffectPathFilter.test(contractReferencePath, ContractReference.class, HierarchicalPath.valueOf("primitive.quantityChange.before.contractReference")), is(false));
        assertThat(EventEffectPathFilter.test(contractReferencePath, ContractReference.class, HierarchicalPath.valueOf("primitive.quantityChange.after.contractReference")), is(true));
    }

    @Test
    void shouldPassBecauseNoRequiredElementsForClass() {
        HierarchicalPath paymentPath = HierarchicalPath.valueOf("eventEffect.payment");

        assertThat(EventEffectPathFilter.test(paymentPath, Payment.class, HierarchicalPath.valueOf("primitive.payment")), is(true));
    }

    @Test
    void shouldFindKnownEffectedContractPaths() {
        List<PathObject<Class<?>>> filteredPaths = new LinkedList<>();

        // inspect all class types, collecting the paths that are filtered out
        RosettaNodeInspector<PathObject<Class<?>>> rosettaNodeInspector = new RosettaNodeInspector<>();
        Visitor<PathObject<Class<?>>> collectFilteredPathVisitor = getCollectEffectedContractPathsVisitor(filteredPaths);
        rosettaNodeInspector.inspect(PathTypeNode.root(Event.class), collectFilteredPathVisitor);

        assertThat(filteredPaths, hasSize(3));
        assertThat(filteredPaths.stream()
                        .map(o -> o.getHierarchicalPath().map(HierarchicalPath::buildPath).orElse(""))
                        .collect(Collectors.toList()),
                   hasItems("primitive.quantityChange.before",
                            "primitive.termsChange.before",
                            "primitive.exercise.before"));
    }

    private Visitor<PathObject<Class<?>>> getCollectEffectedContractPathsVisitor(List<PathObject<Class<?>>> capture) {
        return (n) -> {
            Class<?> forClass = n.get().getObject();
            List<String> inspectedPath = n.get().getPath();
            if(ContractOrContractReference.class.isAssignableFrom(forClass) && inspectedPath.containsAll(EventEffectPathFilter.EFFECTED_CONTRACT_REQUIRED_PATHS)) {
                capture.add(n.get());
            }
        };
    }

}
