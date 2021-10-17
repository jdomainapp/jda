package jda.modules.mosar.backend.base.controllers;

import jda.modules.mosar.backend.annotations.Create;
import jda.modules.mosar.backend.annotations.ID;
import jda.modules.mosar.backend.annotations.Modifying;
import jda.modules.mosar.backend.annotations.PagingCondition;
import jda.modules.mosar.backend.annotations.Retrieve;
import jda.modules.mosar.backend.base.models.Identifier;
import jda.modules.mosar.backend.base.models.Page;
import jda.modules.mosar.backend.base.models.PagingModel;

/**
 * Represent a nested (level-1) resource endpoint.
 * @param <TOuter> the outer type
 * @param <TInner> the inner (nested) type
 */
public interface NestedRestfulController<TOuter, TInner> {

    /**
     * Create an object instance of the inner type as owned by the outer instance.
     * @param outerId
     */
    @Create
    TInner createInner(@ID Identifier<?> outerId,
                       @Modifying TInner requestBody);

    /**
     * Retrieve a list of inner object instances owned by the outer.
     * @param outerId
     * @return
     */
    @Retrieve
    Page<TInner> getInnerListByOuterId(
            @ID Identifier<?> outerId,
            @PagingCondition PagingModel pagingModel);
}
