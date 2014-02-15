README: Mutual Exclusion Lab - Team 60.

Group Construction Rules Violation Scenarios:

1) Violation of Pairwise non-null Intersection Property:
  This property says that there must be atleast one common element between any 2 groups in the system.

  Suppose we consider 2 groups say:
  Group A :  A, B, C   and    Group D : D, E, F.
  These 2 groups do not have any element in common. Hence if we apply Maekawa's Distributed Algorithm
  for Mutual Exclusion, if both A and D request for access to the Critical Section, A won't know about 
  D's request and D also won't know about A's request since they don't have a common element in both
  the groups. Hence, both A and D get access to the Critical Section which is wrong. Hence violation
  of this property results in an erroneous result.

2) Violation of Self-Contain Property:
  This property for all i, Pi is in G(i), i.e all Group i must contain element i.

  Consider the scenario where Group A doesn't contain A. Say Group A contains B, C and D.
  Also consider Group F contains A, F and G.

3) Violation of Equal Effort property:
   This property says that all the groups must have equal number of elements equal to the square root
   of N. 
   Consider Group A has only one element A. Then it will violate the non-null intersection property with some
   groups not containing A.

4) Violation of Equal Responsibility property:
   This property states that an element must be contained in equal number of groups equal to the square root of
   N. Again, consider A is part of only one group say Group A. This again violates the non-null intersection 
   property since some groups won't have any elements in common.
  
